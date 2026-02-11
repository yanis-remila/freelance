package com.example.freelance.model;


import com.example.freelance.model.enums.ValidationStatut;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "validations")
@Data
@NoArgsConstructor
public class Validation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateValidation;

    @Column(length = 2000)
    private String commentaire;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ValidationStatut statut;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @ManyToOne
    @JoinColumn(name = "freelancer_id", nullable = false)
    private Freelancer freelancer;

    @ManyToOne
    @JoinColumn(name = "gerant_id", nullable = false)
    private Gerant gerant;
}
