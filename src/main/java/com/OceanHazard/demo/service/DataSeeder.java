package com.OceanHazard.demo.service;

import com.OceanHazard.demo.entity.HazardReport;
import com.OceanHazard.demo.service.HazardReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Seeds the database with dummy hazard reports for development/testing.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private HazardReportService hazardReportService;

    @Override
    public void run(String... args) throws Exception {
        if (hazardReportService.getAllReports().isEmpty()) {
            System.out.println("No existing reports found. Seeding database with dummy data...");

            HazardReport report1 = new HazardReport(
                    "High Waves", 12.9716, 77.5946,
                    "High waves reported near Bengaluru.", "citizen1", null
            );
            report1.status = "PENDING";
            report1.submittedAt = LocalDateTime.now().minusHours(1);

            HazardReport report2 = new HazardReport(
                    "Coastal Flooding", 19.0760, 72.8777,
                    "Minor coastal flooding in Mumbai.", "citizen2", null
            );
            report2.status = "APPROVED";
            report2.submittedAt = LocalDateTime.now().minusHours(5);

            HazardReport report3 = new HazardReport(
                    "Unusual Tides", 28.7041, 77.1025,
                    "Unusual tide patterns observed in Delhi.", "citizen3", null
            );
            report3.status = "REJECTED";
            report3.submittedAt = LocalDateTime.now().minusDays(1);

            HazardReport report4 = new HazardReport(
                    "High Waves", 19.0760, 72.8777,
                    "Another report of high waves from Mumbai.", "citizen4", null
            );
            report4.status = "PENDING";
            report4.submittedAt = LocalDateTime.now().minusMinutes(30);

            HazardReport report5 = new HazardReport(
                    "Tsunami Alert (False)", 12.9716, 77.5946,
                    "User reported a tsunami, but it was a false alarm.", "citizen5", null
            );
            report5.status = "REJECTED";
            report5.submittedAt = LocalDateTime.now().minusHours(10);

            HazardReport report6 = new HazardReport(
                    "Coastal Flooding", 19.1760, 72.8777,
                    "More flooding near the previous Mumbai report.", "citizen6", null
            );
            report6.status = "APPROVED";
            report6.submittedAt = LocalDateTime.now().minusHours(2);

            hazardReportService.submitReport(report1);
            hazardReportService.submitReport(report2);
            hazardReportService.submitReport(report3);
            hazardReportService.submitReport(report4);
            hazardReportService.submitReport(report5);
            hazardReportService.submitReport(report6);

            System.out.println("Dummy data seeding complete.");
        } else {
            System.out.println("Database already contains data. Skipping seeding.");
        }
    }
}
