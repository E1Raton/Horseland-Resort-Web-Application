package com.software_design.horseland.repository;

import com.software_design.horseland.model.NotificationPreference;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NotificationPreferenceRepository extends JpaRepository<NotificationPreference, UUID> {
    List<NotificationPreference> findByUserId(UUID userId);
    List<NotificationPreference> findByActivityId(UUID activityId);
    NotificationPreference findByUserIdAndActivityId(UUID userId, UUID activityId);
}
