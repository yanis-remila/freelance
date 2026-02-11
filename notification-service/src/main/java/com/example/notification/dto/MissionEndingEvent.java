package com.example.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Evenement Kafka envoyé quand une mission arrive à sa fin.
 * Produit par le microservice freelance, consommé par notification-service.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MissionEndingEvent {

    private Long missionId;
    private Long freelancerId;
    private String freelancerEmail;
    private String missionTitre;
    private LocalDate dateFin;
    private Integer joursRestants;

    // Timestamp de création de l'événement
    private Long timestamp;

    public MissionEndingEvent(Long missionId, Long freelancerId, String freelancerEmail,
                               String missionTitre, LocalDate dateFin, Integer joursRestants) {
        this.missionId = missionId;
        this.freelancerId = freelancerId;
        this.freelancerEmail = freelancerEmail;
        this.missionTitre = missionTitre;
        this.dateFin = dateFin;
        this.joursRestants = joursRestants;
        this.timestamp = System.currentTimeMillis();
    }
}
