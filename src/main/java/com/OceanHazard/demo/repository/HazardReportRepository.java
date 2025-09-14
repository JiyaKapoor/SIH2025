package com.OceanHazard.demo.repository;

import com.OceanHazard.demo.entity.HazardReport;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface HazardReportRepository extends JpaRepository<HazardReport, UUID> {

    // Get all reports submitted by a specific user
    List<HazardReport> findBySubmittedBy(String username);

    // Get reports within a bounding box (for nearby location search)
    List<HazardReport> findByLatitudeBetweenAndLongitudeBetween(
            double minLat, double maxLat, double minLon, double maxLon
    );

}

