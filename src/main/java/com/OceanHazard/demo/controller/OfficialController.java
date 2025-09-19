package com.OceanHazard.demo.controller;

import com.OceanHazard.demo.entity.HazardReport;
import com.OceanHazard.demo.service.HazardReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/official")// /official/reports
public class OfficialController {

    @Autowired
    private HazardReportService hazardReportService;

    // ✅ View all hazard reports
    @GetMapping("/reports")
    public List<HazardReport> getAllReports() {
        return hazardReportService.getAllReports();
    }

    // ✅ Approve a report
    @PutMapping("/report/{id}/approve")
    public String approveReport(@PathVariable UUID id, Authentication auth) {
        HazardReport report = hazardReportService.getReportById(id);
        if (report == null) {
            return "❌ Report not found!";
        }

        // Update status using official workflow
        hazardReportService.updateReportStatus(id, "APPROVED");

        return "✅ Report " + id + " approved by " + auth.getName();
    }


    // ✅ Reject a report
    @PutMapping("/report/{id}/reject")
    public String rejectReport(@PathVariable UUID id, Authentication auth) {
        HazardReport report = hazardReportService.getReportById(id);
        if (report == null) {
            return "❌ Report not found!";
        }

        // Update status using official workflow
        hazardReportService.updateReportStatus(id, "REJECTED");

        return "✅ Report " + id + " rejected by " + auth.getName();
    }

}

