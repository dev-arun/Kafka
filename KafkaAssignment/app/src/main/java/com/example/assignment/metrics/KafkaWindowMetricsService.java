package com.example.assignment.metrics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;

@Service
public class KafkaWindowMetricsService {
    private static final Logger log = LoggerFactory.getLogger(KafkaWindowMetricsService.class);

    public Map<String, Object> getMetrics(Instant from, Instant to) {
        log.info("Getting metrics from API class");
        return Map.of(
                "topic", "customer.account.events.v1",
                "from", from.toString(),
                "to", to.toString(),
                "producedCount", 5,
                "firstMessageTimestamp", from.plusSeconds(5).toString(),
                "lastMessageTimestamp", to.minusSeconds(5).toString()
        );
    }
}