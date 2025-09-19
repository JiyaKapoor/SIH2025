package com.OceanHazard.demo.service;

import org.springframework.web.client.RestTemplate;
import com.OceanHazard.demo.entity.FilterData;
import com.OceanHazard.demo.entity.HazardReport;
import com.OceanHazard.demo.repository.HazardReportRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class AlertService {

    private final RestTemplate restTemplate;
    private final HazardReportRepository hazardReportRepository;

    private final String INCOIS_MASTER_URL =
            "https://tsunami.incois.gov.in/itews/DSSProducts/OPR/past90days.json";

    public AlertService(RestTemplate restTemplate,
                        HazardReportRepository hazardReportRepository) {
        this.restTemplate = restTemplate;
        this.hazardReportRepository = hazardReportRepository;
    }

    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Earth radius in km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    public List<String> fetchFilteredAlerts(FilterData filter) {
        // ✅ Apply defaults if fields missing
        if (filter == null) {
            filter = new FilterData();
        }
        if (filter.maxDaysOld == null) {
            filter.maxDaysOld = 7;   // last 7 days by default
        }
        if (filter.radiusKm == null) {
            filter.radiusKm = 50.0;  // default 50 km radius
        }

        List<String> filteredTexts = new ArrayList<>();

        // ---------- 1. Fetch from INCOIS ----------
        try {
            String responseBody = restTemplate.getForObject(INCOIS_MASTER_URL, String.class);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode masterData = mapper.readTree(responseBody);
            JsonNode events = masterData.get("datasets");

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            for (JsonNode event : events) {
                String region = event.path("REGIONNAME").asText("");
                double magnitude = event.path("MAGNITUDE").asDouble(0.0);
                String source = event.path("SOURCE").asText("");
                String origTime = event.path("ORIGINTIME").asText("");

                double eventLat = event.path("LATITUDE").asDouble(0.0);
                double eventLon = event.path("LONGITUDE").asDouble(0.0);

                String disasterType = magnitude >= 6.5 || region.toLowerCase().contains("tsunami")
                        ? "Tsunami" : "Earthquake";

                boolean matchesDisaster = filter.disasterType == null ||
                        disasterType.equalsIgnoreCase(filter.disasterType);

                boolean matchesSource = filter.source == null ||
                        source.toLowerCase().contains(filter.source.toLowerCase());

                boolean matchesDate = filter.date == null || origTime.contains(filter.date);

                boolean matchesLocation = true;
                if (filter.latitude != null && filter.longitude != null) {
                    double distance = haversine(filter.latitude, filter.longitude, eventLat, eventLon);
                    matchesLocation = distance <= filter.radiusKm;
                }

                boolean matchesTime = true;
                if (filter.maxDaysOld != null) {
                    try {
                        LocalDateTime eventTime = LocalDateTime.parse(origTime, formatter);
                        LocalDateTime cutoff = LocalDateTime.now().minusDays(filter.maxDaysOld);
                        matchesTime = eventTime.isAfter(cutoff);
                    } catch (Exception e) {
                        matchesTime = true;
                    }
                }

                if (matchesDisaster && matchesSource && matchesDate && matchesLocation && matchesTime) {
                    String text = String.format(
                            "[EXTERNAL] Region: %s | Disaster: %s | Magnitude: %.1f | Date: %s | Source: %s | Lat: %.2f | Lon: %.2f",
                            region, disasterType, magnitude, origTime, source, eventLat, eventLon
                    );
                    filteredTexts.add(text);
                }
            }
        } catch (Exception e) {
            System.out.println("❌ Failed to fetch INCOIS alerts: " + e.getMessage());
        }

        // ---------- 2. Fetch from hazard_reports DB ----------
        List<HazardReport> reports = hazardReportRepository.findAll();
        for (HazardReport report : reports) {
            boolean matchesType = filter.disasterType == null ||
                    report.eventType.equalsIgnoreCase(filter.disasterType);

            boolean matchesDate = filter.date == null ||
                    report.submittedAt.toLocalDate().toString().contains(filter.date);

            boolean matchesLocation = true;
            if (filter.latitude != null && filter.longitude != null) {
                double distance = haversine(filter.latitude, filter.longitude,
                        report.latitude, report.longitude);
                matchesLocation = distance <= filter.radiusKm;
            }

            boolean matchesTime = true;
            if (filter.maxDaysOld != null) {
                LocalDateTime cutoff = LocalDateTime.now().minusDays(filter.maxDaysOld);
                matchesTime = report.submittedAt.isAfter(cutoff);
            }

            if (matchesType && matchesDate && matchesLocation && matchesTime) {
                String text = String.format(
                        "[INTERNAL] Event: %s | Description: %s | Status: %s | Date: %s | Lat: %.2f | Lon: %.2f | By: %s | Media: %s",
                        report.eventType, report.description, report.status,
                        report.submittedAt, report.latitude, report.longitude,
                        report.submittedBy, report.mediaUrl
                );
                filteredTexts.add(text);
            }
        }

        return filteredTexts;
    }
}



