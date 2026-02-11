package com.example.notification.repository;

import com.example.notification.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByFreelancerId(Long freelancerId);

    List<Notification> findByFreelancerIdOrderByCreatedAtDesc(Long freelancerId);

    List<Notification> findByFreelancerIdAndIsReadFalseOrderByCreatedAtDesc(Long freelancerId);

    long countByFreelancerIdAndIsReadFalse(Long freelancerId);

    boolean existsByKafkaEventId(String kafkaEventId);
}
