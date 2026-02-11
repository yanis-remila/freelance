package com.example.freelance.model;


import com.example.freelance.model.enums.MissionStatut;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "missions")
@Data
@NoArgsConstructor
@ToString(exclude = {"freelancer", "competences", "client"})
@EqualsAndHashCode(exclude = {"freelancer", "competences", "client"})
public class Mission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titre;

    @Column(nullable = false, length = 2000)
    private String description;

    private Double budget;

    private String duree;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MissionStatut statut;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    @ManyToOne
    @JoinColumn(name = "freelancer_id", nullable = true)
    @JsonIgnoreProperties({"missions", "competences"})
    private Freelancer freelancer;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "mission_competences",
            joinColumns = @JoinColumn(name = "mission_id"),
            inverseJoinColumns = @JoinColumn(name = "competence_id")
    )
    @JsonIgnoreProperties({"freelancer"})
    private Set<Competence> competences = new HashSet<>();
}
