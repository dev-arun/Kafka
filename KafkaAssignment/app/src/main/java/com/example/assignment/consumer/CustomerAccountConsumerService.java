package com.example.assignment.consumer;

import com.example.assignment.model.CustomerAccountProfile;
import com.example.assignment.repository.CustomerAccountRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.util.Map;
import java.util.logging.Logger;

@Service
public class CustomerAccountConsumerService {

    private static final Logger logger = Logger.getLogger(CustomerAccountConsumerService.class.getName());
    private final CustomerAccountRepository repository;

    public CustomerAccountConsumerService(CustomerAccountRepository repository) {
        this.repository = repository;
    }

    @Transactional
    @KafkaListener(topics = "customer.account.events.v1", groupId = "customer-account-consumer-group")
    public void consume(String message) {
        logger.info("Received message: " + message);
        try {
            archive(message);
            Map<String, Object> data = new ObjectMapper().readValue(message, new TypeReference<>() {});
            CustomerAccountProfile profile = transform(data);
            logger.info("Transformed profile: " + profile);
            repository.save(profile);
            logger.info("Saved profile to DB");
        } catch (Exception e) {
            logger.severe("Failed to process message: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private void archive(String message) throws IOException {
        var archivePath = Paths.get("../archive/customer-account-raw-messages.txt");
        var archiveDir = archivePath.getParent();

        if (!Files.exists(archiveDir)) {
            Files.createDirectories(archiveDir);
        }

        Files.writeString(
                archivePath,
                message + "\n",
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND
        );
    }

    private CustomerAccountProfile transform(Map<String, Object> data) {
        String dob = (String) data.get("dateOfBirth");
        boolean isMinor = Period.between(LocalDate.parse(dob), LocalDate.now()).getYears() < 18;
        boolean isEmployee = data.get("employeeId") != null;

        return new CustomerAccountProfile(
                (String) data.get("customerId"),
                (String) data.get("accountId"),
                (String) data.get("name"),
                isMinor ? "Y" : "N",
                isEmployee ? "Y" : "N",
                Instant.now()
        );
    }
}