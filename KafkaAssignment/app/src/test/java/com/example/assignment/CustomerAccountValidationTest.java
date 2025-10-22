package com.example.assignment;

import com.aventstack.extentreports.*;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import com.example.assignment.model.CustomerAccountProfile;
import com.example.assignment.repository.CustomerAccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Set;



@SpringBootTest
public class CustomerAccountValidationTest extends AbstractTestNGSpringContextTests {

    private static final Logger log = LoggerFactory.getLogger(CustomerAccountValidationTest.class);
    @Autowired
    private CustomerAccountRepository repository;

    @Test
    public void validatePipeline() throws Exception {
        System.out.println("TestNG is executing this test");

        ExtentHtmlReporter htmlReporter = new ExtentHtmlReporter("tests/reports/customer-account-validation-report.html");
        ExtentReports extent = new ExtentReports();
        extent.attachReporter(htmlReporter);

        ExtentTest test = extent.createTest("Kafka Data Pipeline Validation");

        // 1. Validate schema
        File schemaFile = new File("src/test/java/com/example/assignment/resources/customer_account_event.schema.json");
        JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V202012);
        JsonSchema schema = factory.getSchema(new FileInputStream(schemaFile));

        List<String> messages = Files.readAllLines(Paths.get("../archive/customer-account-raw-messages.txt"));
        ObjectMapper mapper = new ObjectMapper();
        boolean schemaValid = true;

        for (String msg : messages) {
            JsonNode node = mapper.readTree(msg);
            Set<ValidationMessage> errors = schema.validate(node);
            if (!errors.isEmpty()) {
                schemaValid = false;
                for (ValidationMessage error : errors) {
                    test.fail("Schema validation failed: " + error.getMessage());
                }
            }
        }

        if (schemaValid) {
            test.pass("All messages conform to JSON schema");
        }


        // 2. Validate DB inserts
        List<CustomerAccountProfile> records = repository.findAll();
        if (records.size() == messages.size()) {
            test.pass("All messages inserted into DB");
        } else {
            test.fail("Mismatch in DB record count vs message count");
        }

        // 3. Validate flags
        boolean flagsValid = true;
        for (CustomerAccountProfile profile : records) {
            String dob = profile.getIngestTs().toString().substring(0, 10);
            int age = Period.between(LocalDate.parse(dob), LocalDate.now()).getYears();
            boolean expectedMinor = age < 18;
            boolean actualMinor = "Y".equals(profile.getMinorFlag());

            if (expectedMinor != actualMinor) {
                flagsValid = false;
                test.fail("Minor flag incorrect for customerId: " + profile.getCustomerId());
            }

            boolean actualEmployee = "Y".equals(profile.getEmployeeFlag());
            if (profile.getEmployeeFlag() == null || (!actualEmployee && !"N".equals(profile.getEmployeeFlag()))) {
                flagsValid = false;
                test.fail("Employee flag invalid for customerId: " + profile.getCustomerId());
            }
        }

        if (flagsValid) {
            test.pass("Minor and Employee flags validated successfully");
        }

        extent.flush();
    }
}