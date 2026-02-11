package com.example.freelance.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "recommandations")
@Data
@NoArgsConstructor
public class Recommandation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;

    @Column(length = 2000)
    private String description;

    private Double score; // par exemple la pertinence calculée

    @ManyToOne
    @JoinColumn(name = "freelancer_id")
    private Freelancer freelancer;

    @ManyToOne
    @JoinColumn(name = "mission_id")
    private Mission mission;
}

