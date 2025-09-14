package com.OceanHazard.demo.service;

import com.OceanHazard.demo.entity.HazardReport;
import com.OceanHazard.demo.repository.HazardReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class HazardReportService {

    @Autowired
    private HazardReportRepository hazardReportRepository;

    // Submit a new hazard report
    public HazardReport submitReport(HazardReport report) {
        return hazardReportRepository.save(report);
    }

    // Get all reports submitted by a specific user
    public List<HazardReport> getReportsByUser(String username) {
        return hazardReportRepository.findBySubmittedBy(username);
    }

    // Delete a report by id (only if user owns it)
    public void deleteReport(UUID id, String username) {
        HazardReport report = hazardReportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found"));
        if (!report.submittedBy.equals(username)) {
            throw new RuntimeException("You can only delete your own reports!");
        }
        hazardReportRepository.delete(report);
    }

    // Get reports near a location within a radius in km
    public List<HazardReport> getReportsNearby(double lat, double lon, double radiusKm) {
        double latDegree = radiusKm / 111.0; // 1 degree latitude ≈ 111 km
        double lonDegree = radiusKm / (111.0 * Math.cos(Math.toRadians(lat))); // longitude correction

        double minLat = lat - latDegree;
        double maxLat = lat + latDegree;
        double minLon = lon - lonDegree;
        double maxLon = lon + lonDegree;

        return hazardReportRepository.findByLatitudeBetweenAndLongitudeBetween(
                minLat, maxLat, minLon, maxLon
        );
    }

    // Get all reports
    public List<HazardReport> getAllReports() {
        return hazardReportRepository.findAll();
    }

    // Get report by ID
    public HazardReport getReportById(UUID id) {
        return hazardReportRepository.findById(id).orElse(null);
    }

    // Delete report (official override)
    public void deleteReportByOfficial(UUID id) {
        hazardReportRepository.deleteById(id);
    }
}
