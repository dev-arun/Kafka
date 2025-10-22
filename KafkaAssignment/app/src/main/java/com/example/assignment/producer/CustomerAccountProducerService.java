package com.example.assignment.producer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class CustomerAccountProducerService {

    private static final Logger logger = Logger.getLogger(CustomerAccountProducerService.class.getName());
    private final KafkaTemplate<String, String> kafkaTemplate;

    public CustomerAccountProducerService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @PostConstruct
    public void produceMessages() {
        try {
            for (int i = 1; i <= 5; i++) {
                String message = generateMessage(i);
                kafkaTemplate.send("customer.account.events.v1", message);
                logger.info("Produced message " + i + ": " + message);
                Thread.sleep(2000);
            }
        } catch (Exception e) {
            logger.severe("Kafka producer failed: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private String generateMessage(int index) throws Exception {
        Map<String, Object> event = Map.of(
                "customerId", "CUST-" + UUID.randomUUID(),
                "accountId", "ACC-" + UUID.randomUUID(),
                "name", "CUST-" + index,
                "dateOfBirth", LocalDate.now().minusYears(20 + index).toString(),
                "employeeId",  "EMP-" + index
        );
        return new ObjectMapper().writeValueAsString(event);
    }
}