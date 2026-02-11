package com.example.freelance.model;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "historiques")
@Data
@NoArgsConstructor
public class Historique {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateAction;

    @Column(length = 2000)
    private String description;

    @ManyToOne
    @JoinColumn(name = "mission_id", nullable = false)
    private Mission mission;

    // Optionnel : si l'historique concerne un client
    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client clientAssocie;

    // Optionnel : si l'historique concerne un freelancer
    @ManyToOne
    @JoinColumn(name = "freelancer_id")
    private Freelancer freelancerAssocie;

    // Optionnel : si l'historique concerne un gérant
    @ManyToOne
    @JoinColumn(name = "gerant_id")
    private Gerant gerantAssocie;
}
