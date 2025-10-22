package com.example.assignment.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "customer_account_profile")
public class CustomerAccountProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String customerId;
    private String accountId;
    private String name;
    private String minorFlag;
    private String employeeFlag;

    @Column(columnDefinition = "TIMESTAMPTZ DEFAULT NOW()")
    private Instant ingestTs;

    public CustomerAccountProfile() {}

    public CustomerAccountProfile(String customerId, String accountId, String name,
                                  String minorFlag, String employeeFlag, Instant ingestTs) {
        this.customerId = customerId;
        this.accountId = accountId;
        this.name = name;
        this.minorFlag = minorFlag;
        this.employeeFlag = employeeFlag;
        this.ingestTs = ingestTs;
    }

    public Instant getIngestTs() {
        return ingestTs;
    }

    public String getMinorFlag() {
        return minorFlag;
    }

    public String getEmployeeFlag() {
        return employeeFlag;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public void setEmployeeFlag(String employeeFlag) {
        this.employeeFlag = employeeFlag;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setMinorFlag(String minorFlag) {
        this.minorFlag = minorFlag;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIngestTs(Instant ingestTs) {
        this.ingestTs = ingestTs;
    }

    public Long getId() {
        return id;
    }

    public String getAccountId() {
        return accountId;
    }

    public String getName() {
        return name;
    }


    // Getters and setters omitted for brevity — let me know if you want them included
}