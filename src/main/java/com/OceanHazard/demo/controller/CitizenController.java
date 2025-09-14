package com.OceanHazard.demo.controller;

import com.OceanHazard.demo.entity.HazardReport;
import com.OceanHazard.demo.service.HazardReportService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/citizen")
public class CitizenController {

    @Autowired
    private HazardReportService hazardReportService; // Inject service properly

    // Submit a new hazard report
    @PostMapping(
            value = "/report",
            consumes = { MediaType.MULTIPART_FORM_DATA_VALUE }
    )
    public HazardReport submitReport(
            @RequestPart("report") String reportJson,
            @RequestPart(value = "media", required = false) MultipartFile media,
            Authentication auth) throws Exception {

        // Convert JSON string into HazardReport object
        ObjectMapper mapper = new ObjectMapper();
        HazardReport report = mapper.readValue(reportJson, HazardReport.class);

        // Override submittedBy with authenticated username
        report.submittedBy = auth.getName();

        if (media != null) {
            // TODO: upload to Cloudinary/S3
            report.mediaUrl = "/uploads/" + media.getOriginalFilename();
        }

        return hazardReportService.submitReport(report);
    }


    // Get reports near the citizen's current location
    @GetMapping("/reports/nearby")
    public List<HazardReport> getReportsNearby(
            @RequestParam double lat,
            @RequestParam double lon,
            @RequestParam(defaultValue = "1.0") double radiusKm) { // default radius 1 km

        return hazardReportService.getReportsNearby(lat, lon, radiusKm); // Use instance
    }

    // Delete a report by ID
    @DeleteMapping("/report/{id}")
    public String deleteReport(@PathVariable UUID id, Authentication auth) {
        hazardReportService.deleteReport(id, auth.getName()); // Use instance
        return "Report deleted successfully!";
    }
}
