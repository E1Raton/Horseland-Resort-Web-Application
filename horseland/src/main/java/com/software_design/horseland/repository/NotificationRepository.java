package com.software_design.horseland.repository;

import com.software_design.horseland.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    List<Notification> findByUserId(UUID userId);
    List<Notification> findByActivityId(UUID activityId);
    Notification findByUserIdAndActivityId(UUID userId, UUID activityId);
}
