package com.example.freelance.controller;

import com.example.freelance.model.FreelancerReport;
import com.example.freelance.service.FreelancerReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reports")
public class FreelancerReportController {

    private final FreelancerReportService reportService;

    @PostMapping("/generate")
    public List<FreelancerReport> generateReports() {
        return reportService.generateAndSaveReports();
    }

    @GetMapping
    public List<FreelancerReport> getReports() {
        return reportService.getAllReports();
    }
}
