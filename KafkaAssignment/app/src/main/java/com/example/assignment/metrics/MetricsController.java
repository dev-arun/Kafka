package com.example.assignment.metrics;

import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/metrics/messages")
public class MetricsController {

    private final KafkaWindowMetricsService service;

    public MetricsController(KafkaWindowMetricsService service) {
        this.service = service;
    }
    @GetMapping
    public Map<String, Object> getMetrics(@RequestParam Instant from, @RequestParam Instant to) {
        return service.getMetrics(from, to);
    }
}