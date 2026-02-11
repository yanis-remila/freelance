package com.example.freelance.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FreelancerStatsDTO {
    private Long id;
    private String nom;
    private String prenom;
    private long totalJours;
    private double montantVerse;
    private double montantPayeParClient;
    private double profit;
}
