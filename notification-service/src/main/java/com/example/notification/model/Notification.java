package com.example.notification.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "freelancer_id", nullable = false)
    private Long freelancerId;

    @Column(name = "mission_id")
    private Long missionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private NotificationType type;

    @Column(name = "message", nullable = false)
    private String message;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "is_read")
    private Boolean isRead = false;

    @Column(name = "sent_via_websocket")
    private Boolean sentViaWebsocket = false;

    @Column(name = "kafka_event_id", unique = true)
    private String kafkaEventId;

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    public Notification(Long freelancerId, Long missionId, NotificationType type, String message) {
        this.freelancerId = freelancerId;
        this.missionId = missionId;
        this.type = type;
        this.message = message;
        this.isRead = false;
        this.sentViaWebsocket = false;
    }
}
