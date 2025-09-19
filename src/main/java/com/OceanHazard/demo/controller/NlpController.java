package com.OceanHazard.demo.controller;

import com.OceanHazard.demo.service.NlpService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/nlp")
public class NlpController {

    private final NlpService nlpService;

    public NlpController(NlpService nlpService) {
        this.nlpService = nlpService;
    }

    // Endpoint to classify alert
    @PostMapping("/classify")
    public String classify(@RequestParam String text, @RequestParam double magnitude) {
        return nlpService.classifyAlert(text, magnitude);
    }

    // Endpoint to summarize alert
    @PostMapping("/summarize")
    public String summarize(@RequestParam String text) {
        return nlpService.summarize(text);
    }
}
