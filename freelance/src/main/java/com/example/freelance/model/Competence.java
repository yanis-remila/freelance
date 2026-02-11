package com.example.freelance.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "competences")
@Data
@NoArgsConstructor
@ToString(exclude = {"freelancer"})
@EqualsAndHashCode(exclude = {"freelancer"})
public class Competence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(length = 2000)
    private String description;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "freelancer_id")
    private Freelancer freelancer;
}

