package com.example.freelance.model;
import com.example.freelance.model.enums.CandidatureStatut;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "candidatures")
@Data
@NoArgsConstructor
public class Candidature {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCandidature;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CandidatureStatut statut;

    @Column(length = 2000)
    private String commentaire;

    @ManyToOne
    @JoinColumn(name = "freelancer_id", nullable = false)
    @JsonIgnoreProperties({"missions", "competences", "notifications"})
    private Freelancer freelancer;

    @ManyToOne
    @JoinColumn(name = "mission_id", nullable = false)
    @JsonIgnoreProperties({"freelancer", "competences", "client"})
    private Mission mission;
}

