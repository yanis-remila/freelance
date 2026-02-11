package com.example.notification.controller;

import com.example.notification.dto.NotificationDTO;
import com.example.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/freelancer/{id}")
    public ResponseEntity<List<NotificationDTO>> getNotificationsByFreelancerId(@PathVariable Long id) {
        log.info("Getting all notifications for freelancer {}", id);
        List<NotificationDTO> notifications = notificationService.getNotificationsByFreelancerId(id);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/freelancer/{id}/unread")
    public ResponseEntity<List<NotificationDTO>> getUnreadNotificationsByFreelancerId(@PathVariable Long id) {
        log.info("Getting unread notifications for freelancer {}", id);
        List<NotificationDTO> notifications = notificationService.getUnreadNotificationsByFreelancerId(id);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/freelancer/{id}/count")
    public ResponseEntity<Map<String, Long>> countUnreadNotifications(@PathVariable Long id) {
        log.info("Counting unread notifications for freelancer {}", id);
        long count = notificationService.countUnreadNotifications(id);
        return ResponseEntity.ok(Map.of("unreadCount", count));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<NotificationDTO> markAsRead(@PathVariable Long id) {
        log.info("Marking notification {} as read", id);
        NotificationDTO notification = notificationService.markAsRead(id);
        return ResponseEntity.ok(notification);
    }

    @PutMapping("/freelancer/{id}/read-all")
    public ResponseEntity<Void> markAllAsRead(@PathVariable Long id) {
        log.info("Marking all notifications as read for freelancer {}", id);
        notificationService.markAllAsRead(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP", "service", "notification-service"));
    }
}
