package com.OceanHazard.demo.controller;

import com.OceanHazard.demo.entity.FilterData;
import com.OceanHazard.demo.service.AlertService;
import com.OceanHazard.demo.service.NlpService;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/alerts")// /alerts/process-filters
public class AlertController {
    private final AlertService alertService;
    private final NlpService nlpService;

    public AlertController(AlertService alertService, NlpService nlpService) {
        this.alertService = alertService;
        this.nlpService = nlpService;
    }

    @PostMapping("/process-filters")
    public List<Map<String, String>> processAlerts(@RequestBody FilterData filterData) {
        List<String> alerts = alertService.fetchFilteredAlerts(filterData);
        List<Map<String, String>> results = new ArrayList<>();

        for (String text : alerts) {
            Map<String, String> map = new HashMap<>();
            map.put("rawText", text);
            map.put("classification", nlpService.classifyAlert(text, 0.0));
            map.put("summary", nlpService.summarize(text));
            results.add(map);
        }
        return results;
    }
}