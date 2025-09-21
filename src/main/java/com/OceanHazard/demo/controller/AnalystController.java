package com.OceanHazard.demo.controller;

import com.OceanHazard.demo.entity.HazardReport;
import com.OceanHazard.demo.service.AnalystService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/analyst")
public class AnalystController {

    @Autowired
    private AnalystService analystService;

    @GetMapping("/stats")
    public Map<String, Long> getDashboardStats() {
        return analystService.getDashboardStats();
    }

    @GetMapping("/hotspots")
    public List<Map<String, Object>> getHotspots() {
        return analystService.getHotspots();
    }

    @GetMapping("/reports")
    public List<HazardReport> getAllReports() {
        return analystService.getAllReports();

    }

    @GetMapping("/hazard-distribution")
    public Map<String, Long> getHazardDistribution() {
        return analystService.getHazardDistribution();
    }

}

