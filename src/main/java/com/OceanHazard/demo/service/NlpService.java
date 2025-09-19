package com.OceanHazard.demo.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class NlpService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String NLP_API_BASE = "http://localhost:8000"; // FastAPI URL

    public String classifyAlert(String text, double magnitude) {
        try {
            String url = NLP_API_BASE + "/classify-alert";
            String json = String.format("{\"text\": \"%s\", \"magnitude\": %f}", text, magnitude);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> request = new HttpEntity<>(json, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode node = mapper.readTree(response.getBody());
                return node.path("best_label").asText("Unknown");
            }
        } catch (Exception e) {
            System.out.println("⚠️ NLP classify error: " + e.getMessage());
        }
        return "Unknown";
    }

    public String summarize(String text) {
        try {
            String url = NLP_API_BASE + "/summarize";
            String json = String.format("{\"text\": \"%s\"}", text);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> request = new HttpEntity<>(json, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode node = mapper.readTree(response.getBody());
                return node.path("summary").asText(text);
            }
        } catch (Exception e) {
            System.out.println("⚠️ NLP summarize error: " + e.getMessage());
        }
        return text;
    }
}
