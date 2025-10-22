package com.example.assignment.repository;

import com.example.assignment.model.CustomerAccountProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerAccountRepository extends JpaRepository<CustomerAccountProfile, String> {
}