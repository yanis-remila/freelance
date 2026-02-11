package com.example.notification.service;

import com.example.notification.dto.MissionEndingEvent;
import com.example.notification.dto.NotificationDTO;
import com.example.notification.model.Notification;
import com.example.notification.model.NotificationType;
import com.example.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Traite un événement de fin de mission reçu de Kafka
     */
    @Transactional
    public void processMissionEndingEvent(MissionEndingEvent event) {
        String eventId = event.getMissionId() + "-" + event.getFreelancerId() + "-RAPPEL_FIN_MISSION";

        // Vérifier si cet événement n'a pas déjà été traité (idempotence)
        if (notificationRepository.existsByKafkaEventId(eventId)) {
            log.info("Event already processed: {}", eventId);
            return;
        }

        String message = String.format(
                "Rappel : Votre mission \"%s\" se termine dans %d jour(s). " +
                "N'oubliez pas de rédiger votre rapport de fin de mission.",
                event.getMissionTitre(),
                event.getJoursRestants()
        );

        Notification notification = new Notification(
                event.getFreelancerId(),
                event.getMissionId(),
                NotificationType.RAPPEL_FIN_MISSION,
                message
        );
        notification.setKafkaEventId(eventId);

        try {
            notification = notificationRepository.save(notification);
            log.info("Notification saved for freelancer {} - mission {}",
                    event.getFreelancerId(), event.getMissionId());
        } catch (DataIntegrityViolationException e) {
            log.info("Duplicate notification detected by unique constraint for eventId: {}", eventId);
            return;
        }

        // Envoyer via WebSocket
        sendNotificationViaWebSocket(notification);
    }

    /**
     * Crée et envoie une notification
     */
    @Transactional
    public Notification createAndSendNotification(Long freelancerId, Long missionId,
                                                   NotificationType type, String message) {
        Notification notification = new Notification(freelancerId, missionId, type, message);
        notification = notificationRepository.save(notification);
        sendNotificationViaWebSocket(notification);
        return notification;
    }

    /**
     * Envoie la notification via WebSocket
     */
    private void sendNotificationViaWebSocket(Notification notification) {
        try {
            NotificationDTO dto = NotificationDTO.fromEntity(notification);
            String destination = "/topic/notifications/" + notification.getFreelancerId();
            messagingTemplate.convertAndSend(destination, dto);

            notification.setSentViaWebsocket(true);
            notificationRepository.save(notification);

            log.info("Notification sent via WebSocket to {}", destination);
        } catch (Exception e) {
            log.error("Failed to send WebSocket notification: {}", e.getMessage());
        }
    }

    public List<NotificationDTO> getNotificationsByFreelancerId(Long freelancerId) {
        return notificationRepository.findByFreelancerIdOrderByCreatedAtDesc(freelancerId)
                .stream()
                .map(NotificationDTO::fromEntity)
                .toList();
    }

    public List<NotificationDTO> getUnreadNotificationsByFreelancerId(Long freelancerId) {
        return notificationRepository.findByFreelancerIdAndIsReadFalseOrderByCreatedAtDesc(freelancerId)
                .stream()
                .map(NotificationDTO::fromEntity)
                .toList();
    }

    public long countUnreadNotifications(Long freelancerId) {
        return notificationRepository.countByFreelancerIdAndIsReadFalse(freelancerId);
    }

    @Transactional
    public NotificationDTO markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found: " + notificationId));

        notification.setIsRead(true);
        notification = notificationRepository.save(notification);

        return NotificationDTO.fromEntity(notification);
    }

    @Transactional
    public void markAllAsRead(Long freelancerId) {
        List<Notification> unreadNotifications = notificationRepository
                .findByFreelancerIdAndIsReadFalseOrderByCreatedAtDesc(freelancerId);

        unreadNotifications.forEach(notification -> notification.setIsRead(true));
        notificationRepository.saveAll(unreadNotifications);
    }
}
