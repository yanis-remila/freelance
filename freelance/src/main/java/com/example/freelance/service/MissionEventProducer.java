package com.example.freelance.service;

import com.example.freelance.dto.MissionEndingEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * Producer Kafka pour publier les événements de mission.
 *
 * Ce service est responsable de publier les événements sur Kafka.
 * Le microservice notification-service consommera ces événements.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MissionEventProducer {

    private final KafkaTemplate<String, MissionEndingEvent> kafkaTemplate;

    private static final String TOPIC_MISSION_ENDING = "mission-ending";
    private static final String TOPIC_MISSION_ASSIGNED = "mission-assigned";

    /**
     * Publie un événement de fin de mission sur Kafka.
     *
     * @param event L'événement contenant les informations de la mission
     */
    public void publishMissionEndingEvent(MissionEndingEvent event) {
        String key = event.getMissionId() + "-" + event.getFreelancerId();

        CompletableFuture<SendResult<String, MissionEndingEvent>> future =
                kafkaTemplate.send(TOPIC_MISSION_ENDING, key, event);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Published mission-ending event: missionId={}, freelancerId={}, partition={}, offset={}",
                        event.getMissionId(),
                        event.getFreelancerId(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            } else {
                log.error("Failed to publish mission-ending event: missionId={}, freelancerId={}, error={}",
                        event.getMissionId(),
                        event.getFreelancerId(),
                        ex.getMessage());
            }
        });
    }

    /**
     * Publie un événement de mission assignée sur Kafka.
     *
     * @param event L'événement contenant les informations de la mission
     */
    public void publishMissionAssignedEvent(MissionEndingEvent event) {
        String key = event.getMissionId() + "-" + event.getFreelancerId();

        CompletableFuture<SendResult<String, MissionEndingEvent>> future =
                kafkaTemplate.send(TOPIC_MISSION_ASSIGNED, key, event);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Published mission-assigned event: missionId={}, freelancerId={}",
                        event.getMissionId(), event.getFreelancerId());
            } else {
                log.error("Failed to publish mission-assigned event: {}", ex.getMessage());
            }
        });
    }
}
