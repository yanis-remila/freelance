package com.example.freelance.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FreelancerRecommendationDTO {
    private Long freelancerId;
    private String nom;
    private String prenom;
    private double score;
}

