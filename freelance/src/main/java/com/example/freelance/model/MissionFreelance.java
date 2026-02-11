package com.example.freelance.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Entité JPA représentant l'association entre une mission et un freelancer.
 *
 * Cette table fait le lien entre :
 * - La table "mission" (les missions disponibles)
 * - La table "freelancer" (les freelancers inscrits)
 *
 * Une mission peut être assignée à plusieurs freelancers.
 * Un freelancer peut avoir plusieurs missions.
 * → Relation Many-to-Many représentée par cette table d'association.
 *
 * Table PostgreSQL : mission_freelance
 *
 * Structure de la table :
 * CREATE TABLE mission_freelance (
 *     id BIGSERIAL PRIMARY KEY,
 *     mission_id BIGINT NOT NULL,
 *     freelancer_id BIGINT NOT NULL,
 *     date_debut DATE,
 *     date_fin DATE,
 *     notification_envoyee BOOLEAN DEFAULT false
 * );
 *
 * Annotations Lombok :
 * - @Data : Génère getters, setters, equals, hashCode, toString
 * - @NoArgsConstructor : Génère un constructeur sans arguments (requis par JPA)
 */
@Entity
@Table(name = "mission_freelance")
@Data
@NoArgsConstructor
public class MissionFreelance {

    /**
     * Identifiant unique de l'association mission-freelancer.
     *
     * @Id : Indique que c'est la clé primaire
     * @GeneratedValue(IDENTITY) : Auto-increment géré par PostgreSQL (BIGSERIAL)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Référence vers la mission assignée.
     *
     * On stocke l'ID plutôt qu'une relation @ManyToOne pour simplifier.
     * Dans une architecture plus complète, on aurait :
     *
     * @ManyToOne
     * @JoinColumn(name = "mission_id")
     * private Mission mission;
     */
    @Column(name = "mission_id", nullable = false)
    private Long missionId;

    /**
     * Référence vers le freelancer assigné à cette mission.
     *
     * Idem, on stocke l'ID pour simplifier.
     */
    @Column(name = "freelancer_id", nullable = false)
    private Long freelancerId;

    // ========================================================================
    // CHAMPS AJOUTÉS POUR LE SYSTÈME DE NOTIFICATION
    // ========================================================================

    /**
     * Date de début de la mission pour ce freelancer.
     *
     * LocalDate : Type Java 8+ pour représenter une date sans heure (YYYY-MM-DD).
     * Mappé en type DATE dans PostgreSQL.
     *
     * Exemple : 2026-01-15
     */

    @Column(name = "date_debut")
    private LocalDate dateDebut;

    /**
     * Date de fin prévue de la mission.
     *
     * Cette date est utilisée par le scheduler pour envoyer les rappels.
     * Le scheduler cherche les missions où dateFin = aujourd'hui + 3 jours.
     *
     * Exemple : 2026-02-15
     */
    @Column(name = "date_fin")
    private LocalDate dateFin;

    /**
     * Flag indiquant si la notification de rappel a été envoyée.
     *
     * Valeur par défaut : false (pas encore notifié)
     *
     * Ce flag est CRUCIAL pour éviter d'envoyer plusieurs notifications
     * pour la même mission. Quand le scheduler envoie une notification,
     * il passe ce flag à true.
     *
     * Requête du scheduler :
     * WHERE date_fin = :targetDate AND notification_envoyee = false
     *
     * Sans ce flag, si le scheduler s'exécute plusieurs fois dans la journée,
     * le freelancer recevrait plusieurs notifications identiques.
     */
    @Column(name = "notification_envoyee")
    private Boolean notificationEnvoyee = false;
}
