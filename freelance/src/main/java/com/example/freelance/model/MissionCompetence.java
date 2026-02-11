package com.example.freelance.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "mission_competences")
@Data
@NoArgsConstructor
public class MissionCompetence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "mission_id", nullable = false)
    private Mission mission;

    @ManyToOne
    @JoinColumn(name = "competence_id", nullable = false)
    private Competence competence;

    public MissionCompetence(Mission mission, Competence competence) {
        this.mission = mission;
        this.competence = competence;
    }
}
