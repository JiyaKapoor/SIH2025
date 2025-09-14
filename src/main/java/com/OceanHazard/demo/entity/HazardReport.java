package com.OceanHazard.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "hazard_reports")
public class HazardReport {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public UUID id;   // unique report ID

    @Column(nullable = false)
    public  String eventType;   // e.g., "Flood", "Cyclone", "High Waves"

    @Column(nullable = false)
    public double latitude;

    @Column(nullable = false)
    public double longitude;

    @Column(length = 2000)
    public String description;

    // ✅ submittedBy is filled automatically from Authentication (citizen username)
    @Column(nullable = false, updatable = false)
    public String submittedBy;

    // ✅ URL pointing to media uploaded (image/video)
    public String mediaUrl;

    // ✅ status can be updated later by officials/analysts
    @Column(nullable = false)
    public String status = "PENDING"; // default when citizen submits

    // ✅ automatically set when the report is created
    @Column(nullable = false, updatable = false)
    public LocalDateTime submittedAt = LocalDateTime.now();



    public HazardReport() {
    }

    public HazardReport(String eventType, double latitude, double longitude,
                        String description, String submittedBy, String mediaUrl,
                        String status, LocalDateTime submittedAt) {
        this.eventType = eventType;
        this.latitude = latitude;
        this.longitude = longitude;
        this.description = description;
        this.submittedBy = submittedBy;
        this.mediaUrl = mediaUrl;
        this.status = status;
        this.submittedAt = submittedAt;
    }

    // ----------- toString() -----------

    @Override
    public String toString() {
        return "HazardReport{" +
                "id=" + id +
                ", eventType='" + eventType + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", description='" + description + '\'' +
                ", submittedBy='" + submittedBy + '\'' +
                ", mediaUrl='" + mediaUrl + '\'' +
                ", status='" + status + '\'' +
                ", submittedAt=" + submittedAt +
                '}';
    }
}
