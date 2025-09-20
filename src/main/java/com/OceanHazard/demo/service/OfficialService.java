package com.OceanHazard.demo.service;

import com.OceanHazard.demo.entity.HazardReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OfficialService {

    @Autowired
    private HazardReportService hazardReportService;

    /**
     * Calculates key statistics for the analyst dashboard.
     * @return A map containing statistics like total reports, pending reports, etc.
     */
    public Map<String, Long> getDashboardStats() {
        List<HazardReport> reports = hazardReportService.getAllReports();
        LocalDateTime twentyFourHoursAgo = LocalDateTime.now().minusHours(24);

        long totalReports = reports.size();
        long pendingReports = reports.stream().filter(r -> "PENDING".equalsIgnoreCase(r.status)).count();
        long approvedReports = reports.stream().filter(r -> "APPROVED".equalsIgnoreCase(r.status)).count();
        long rejectedReports = reports.stream().filter(r -> "REJECTED".equalsIgnoreCase(r.status)).count();
        long reportsToday = reports.stream().filter(r -> r.submittedAt != null && r.submittedAt.isAfter(twentyFourHoursAgo)).count();

        Map<String, Long> stats = new HashMap<>();
        stats.put("totalReports", totalReports);
        stats.put("pendingReports", pendingReports);
        stats.put("approvedReports", approvedReports);
        stats.put("rejectedReports", rejectedReports);
        stats.put("reportsToday", reportsToday);

        return stats;
    }

    /**
     * Identifies geographical hotspots based on report density.
     * @return A list of maps, where each map represents a hotspot with lat, lon, and intensity.
     */
    public List<Map<String, Object>> getHotspots() {
        List<HazardReport> reports = hazardReportService.getAllReports();
        Map<String, Map<String, Object>> hotspotsMap = new HashMap<>();

        for (HazardReport report : reports) {
            // Create a key by rounding coordinates to group close reports.
            // Adjust the precision (e.g., 100.0 for ~1km) as needed.
            String key = Math.round(report.latitude * 100.0) / 100.0 + "," + Math.round(report.longitude * 100.0) / 100.0;

            hotspotsMap.computeIfAbsent(key, k -> {
                Map<String, Object> hotspot = new HashMap<>();
                hotspot.put("latitude", report.latitude);
                hotspot.put("longitude", report.longitude);
                hotspot.put("intensity", 0);
                return hotspot;
            });

            Map<String, Object> hotspot = hotspotsMap.get(key);
            hotspot.put("intensity", (int) hotspot.get("intensity") + 1);
        }

        return new ArrayList<>(hotspotsMap.values());
    }
}