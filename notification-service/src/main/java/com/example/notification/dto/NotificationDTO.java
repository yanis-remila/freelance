package com.example.notification.dto;

import com.example.notification.model.Notification;
import com.example.notification.model.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDTO {

    private Long id;
    private Long freelancerId;
    private Long missionId;
    private NotificationType type;
    private String message;
    private LocalDateTime createdAt;
    private Boolean isRead;

    public static NotificationDTO fromEntity(Notification notification) {
        return new NotificationDTO(
                notification.getId(),
                notification.getFreelancerId(),
                notification.getMissionId(),
                notification.getType(),
                notification.getMessage(),
                notification.getCreatedAt(),
                notification.getIsRead()
        );
    }
}
