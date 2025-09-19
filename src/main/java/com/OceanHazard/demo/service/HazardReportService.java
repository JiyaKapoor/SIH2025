package com.OceanHazard.demo.service;

import com.OceanHazard.demo.entity.HazardReport;
import com.OceanHazard.demo.entity.User;
import com.OceanHazard.demo.repository.HazardReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class HazardReportService {

    @Autowired
    private HazardReportRepository hazardReportRepository;

    // Save hazard with optional media
    // Save hazard with optional media + submitting user
    public HazardReport saveHazard(HazardReport hazard, MultipartFile media, User user) throws IOException {
        if (media != null && !media.isEmpty()) {
            hazard.mediaUrl = "/uploads/" + media.getOriginalFilename();
        }

        if (hazard.status == null) {
            hazard.status = "PENDING";
        }

        if (hazard.submittedAt == null) {
            hazard.submittedAt = LocalDateTime.now();
        }

        // Link report to the submitting user
        hazard.submittedBy = user.username;

        return hazardReportRepository.save(hazard);
    }

    // ✅ Get reports near given location
    public List<HazardReport> getReportsNearby(double lat, double lon, double radiusKm) {
        List<HazardReport> allReports = hazardReportRepository.findAll();

        return allReports.stream()
                .filter(report -> distanceKm(lat, lon, report.latitude, report.longitude) <= radiusKm)
                .collect(Collectors.toList());
    }

    // ✅ Delete report only if submitted by current user
    public void deleteReport(UUID id, String currentUsername) {
        HazardReport report = hazardReportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found"));

        if (!report.submittedBy.equals(currentUsername)) {
            throw new RuntimeException("You are not authorized to delete this report");
        }

        hazardReportRepository.delete(report);
    }

    // ------------------- Official Operations -------------------

    // Fetch all reports (for official dashboard)
    public List<HazardReport> getAllReports() {
        return hazardReportRepository.findAll();
    }

    // Get a single report by ID
    public HazardReport getReportById(UUID id) {
        return hazardReportRepository.findById(id).orElse(null);
    }

    // Approve or reject a report (official action)
    public HazardReport submitReport(HazardReport report) {
        return hazardReportRepository.save(report);
    }

    // Convenience method: update report status and save
    public HazardReport updateReportStatus(UUID id, String status) {
        HazardReport report = getReportById(id);
        if (report != null) {
            report.status = status;
            return hazardReportRepository.save(report);
        }
        return null;
    }

    // Utility method: Haversine formula for distance between 2 coords
    private double distanceKm(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Earth radius in km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}


