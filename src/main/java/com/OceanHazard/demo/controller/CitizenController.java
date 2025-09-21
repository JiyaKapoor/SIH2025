package com.OceanHazard.demo.controller;

import com.OceanHazard.demo.entity.FilterData;
import com.OceanHazard.demo.entity.HazardReport;
import com.OceanHazard.demo.entity.User;
import com.OceanHazard.demo.service.AlertService;
import com.OceanHazard.demo.service.HazardReportService;
import com.OceanHazard.demo.service.OfficialService;
import com.OceanHazard.demo.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/citizen")
public class CitizenController {
    @Autowired
    private OfficialService officialService;

    @Autowired
    private HazardReportService hazardReportService;

    @Autowired
    private UserService userService;

    private final AlertService alertService;

    public CitizenController(AlertService alertService) {
        this.alertService = alertService;
    }

    // ---------------- Submit Hazard Report ----------------
    @PostMapping("/report")
    public ResponseEntity<?> submitHazard(
            @RequestParam(value = "media", required = false) MultipartFile media,
            @RequestParam("report") String reportJson,
            @RequestHeader("Authorization") String authHeader) {

        try {
            // 1. Decode Basic Auth
            String base64Credentials = authHeader.substring("Basic ".length()).trim();
            String credentials = new String(Base64.getDecoder().decode(base64Credentials));
            String[] values = credentials.split(":", 2);

            String username = values[0];
            String password = values[1];

            // 2. Validate user
            User user = userService.validateAndGetUser(username, password);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("❌ Invalid credentials");
            }

            // 3. Check role
            if (!user.roles.contains("USER")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("❌ You are not allowed to submit hazard reports");
            }

            // 4. Parse report JSON
            ObjectMapper mapper = new ObjectMapper();
            HazardReport hazard = mapper.readValue(reportJson, HazardReport.class);

            // 5. Save hazard report with user + optional media
            HazardReport saved = hazardReportService.saveHazard(hazard, media, user);

            return ResponseEntity.ok(saved);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("❌ Invalid report JSON: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("❌ Error submitting hazard: " + e.getMessage());
        }
    }


    // ---------------- Dashboard Feed ----------------
    @PostMapping("/dashboard-feed")
    public List<Map<String, String>> getDashboardFeed(@RequestBody(required = false) FilterData filterData) {
        if (filterData == null) {
            filterData = new FilterData();
            filterData.maxDaysOld = 7;   // default = last 7 days
            filterData.radiusKm = 50.0;  // default = 50 km
        }

        return alertService.fetchFilteredAlerts(filterData)
                .stream()
                .map(text -> {
                    Map<String, String> map = new HashMap<>();
                    map.put("rawText", text);
                    return map;
                })
                .collect(Collectors.toList());
    }

    // ---------------- Get Reports Nearby ----------------
    @GetMapping("/reports/nearby")
    public List<HazardReport> getReportsNearby(
            @RequestParam double lat,
            @RequestParam double lon,
            @RequestParam(defaultValue = "1.0") double radiusKm) {

        return hazardReportService.getReportsNearby(lat, lon, radiusKm);
    }
    @GetMapping("/hotspots")
    public List<Map<String, Object>> getHotspots() {
        return officialService.getHotspots();
    }
    // ---------------- Delete Report ----------------
    @DeleteMapping("/report/{id}")
    public String deleteReport(@PathVariable UUID id, Authentication auth) {
        hazardReportService.deleteReport(id, auth.getName());
        return "✅ Report deleted successfully!";
    }
}
