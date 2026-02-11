package com.example.freelance.model;

import com.example.freelance.model.enums.FreelancerStatus;
import com.example.freelance.model.enums.Gender;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Set;

@Entity
@Table(name = "freelancers")
@Data
@NoArgsConstructor
@ToString(exclude = {"missions", "competences"})
@EqualsAndHashCode(exclude = {"missions", "competences"})
public class Freelancer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String prenom;

    @Column(nullable = false, unique = true)
    private String email;

    @JsonIgnore
    @OneToMany(mappedBy = "freelancer")
    private Set<Mission> missions;

    @JsonIgnore
    @OneToMany(mappedBy = "freelancer")
    private Set<Competence> competences;

    private Double experience;

    private Integer age;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    private String profil;

    @Enumerated(EnumType.STRING)
    private FreelancerStatus status;

}
