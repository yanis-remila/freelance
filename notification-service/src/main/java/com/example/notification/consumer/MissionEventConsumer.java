package com.example.notification.consumer;

import com.example.notification.dto.MissionEndingEvent;
import com.example.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Consumer Kafka qui écoute les événements de fin de mission.
 *
 * Quand le microservice freelance détecte qu'une mission arrive à sa fin,
 * il publie un événement sur le topic "mission-ending".
 * Ce consumer reçoit l'événement et crée/envoie la notification.
 *
 * Avantages de cette architecture:
 * - Découplage: le service freelance n'a pas besoin de connaître le service notification
 * - Scalabilité: on peut avoir plusieurs instances de ce consumer
 * - Fiabilité: si ce service tombe, les messages restent dans Kafka
 * - Rejouabilité: on peut rejouer les messages en cas de problème
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MissionEventConsumer {

    private final NotificationService notificationService;

    /**
     * Écoute le topic "mission-ending" pour les rappels de fin de mission.
     *
     * @KafkaListener configure automatiquement le consumer.
     * - topics: le nom du topic Kafka à écouter
     * - groupId: identifiant du groupe de consumers (pour le load balancing)
     * - concurrency: nombre de threads pour traiter les messages en parallèle
     *
     * Note: Réduit à 1 thread pour simplifier les tests et le débogage.
     * Peut être augmenté en production si nécessaire (ex: concurrency="3" pour > 50 missions).
     */
    @KafkaListener(
            topics = "mission-ending",
            groupId = "notification-group",
            concurrency = "1"
    )
    public void handleMissionEndingEvent(MissionEndingEvent event) {
        log.info("Received mission-ending event: missionId={}, freelancerId={}, joursRestants={}",
                event.getMissionId(), event.getFreelancerId(), event.getJoursRestants());

        try {
            notificationService.processMissionEndingEvent(event);
            log.info("Successfully processed mission-ending event for mission {}",
                    event.getMissionId());
        } catch (Exception e) {
            log.error("Failed to process mission-ending event for mission {}: {}",
                    event.getMissionId(), e.getMessage(), e);
            // L'exception est propagée pour que Kafka puisse retry
            throw e;
        }
    }

    /**
     * Écoute le topic "mission-assigned" pour les nouvelles missions assignées.
     */
    @KafkaListener(
            topics = "mission-assigned",
            groupId = "notification-group"
    )
    public void handleMissionAssignedEvent(MissionEndingEvent event) {
        log.info("Received mission-assigned event: missionId={}, freelancerId={}",
                event.getMissionId(), event.getFreelancerId());

        try {
            String message = String.format(
                    "Nouvelle mission assignée : \"%s\". Consultez les détails dans votre espace.",
                    event.getMissionTitre()
            );

            notificationService.createAndSendNotification(
                    event.getFreelancerId(),
                    event.getMissionId(),
                    com.example.notification.model.NotificationType.MISSION_ASSIGNEE,
                    message
            );
        } catch (Exception e) {
            log.error("Failed to process mission-assigned event: {}", e.getMessage(), e);
            throw e;
        }
    }
}
