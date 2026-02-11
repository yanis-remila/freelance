package com.example.freelance.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Entity
@Table(name = "evaluations")
@Data
@NoArgsConstructor
public class Evaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double note;

    @Column(length = 2000)
    private String commentaire;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dateEvaluation;

    @ManyToOne
    @JoinColumn(name = "mission_id", nullable = false)
    @JsonIgnoreProperties({"freelancer", "competences", "client"})
    private Mission mission;

    @Column(length = 2000)
    private String detail;

}
