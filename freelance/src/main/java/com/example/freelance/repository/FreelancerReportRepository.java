package com.example.freelance.repository;

import com.example.freelance.model.FreelancerReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FreelancerReportRepository extends JpaRepository<FreelancerReport, Long> {
}


