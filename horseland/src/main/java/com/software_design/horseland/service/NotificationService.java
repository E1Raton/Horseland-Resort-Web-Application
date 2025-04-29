package com.software_design.horseland.service;

import com.software_design.horseland.model.Activity;
import com.software_design.horseland.model.Notification;
import com.software_design.horseland.model.NotificationPreference;
import com.software_design.horseland.repository.NotificationPreferenceRepository;
import com.software_design.horseland.repository.NotificationRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.*;

@Service
@AllArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final NotificationPreferenceRepository notificationPreferenceRepository;

    private static final int DAYS_INTERVAL = 30;
    private final ActivityService activityService;

    public Notification getNotificationById(UUID id) {
        return notificationRepository.findById(id).orElse(null);
    }

    public List<Notification> getNotificationsByUserId(UUID userId) {
        return notificationRepository.findByUserId(userId);
    }

    public Notification addNotification(Notification notification) {
        return notificationRepository.save(notification);
    }

    public NotificationPreference addNotificationPreference(NotificationPreference notificationPreference) {
        return notificationPreferenceRepository.save(notificationPreference);
    }

    public List<Notification> addNotifications(UUID userId) {

        LocalDate today = LocalDate.now();

        return activityService.getByParticipantId(userId).stream()
                .filter(activity -> Math.abs(Period.between(today, activity.getStartDate()).getDays()) < DAYS_INTERVAL)
                .map(activity -> processActivityNotification(userId, activity, today))
                .filter(Objects::nonNull)
                .toList();
    }

    private Notification processActivityNotification(UUID userId, Activity activity, LocalDate today) {
        UUID activityId = activity.getId();
        NotificationPreference preference = notificationPreferenceRepository.findByUserIdAndActivityId(userId, activityId);

        if (preference != null && Boolean.TRUE.equals(preference.getActive())) {
            return upsertNotification(userId, activity, today);
        }

        if (preference == null) {
            createAndSaveNotificationPreference(userId, activityId);
            return createAndSaveNotification(userId, activity, today);
        }

        return null; // No notification needed if preference exists but is inactive
    }

    private Notification upsertNotification(UUID userId, Activity activity, LocalDate today) {
        Notification existing = notificationRepository.findByUserIdAndActivityId(userId, activity.getId());

        return (existing == null)
                ? createAndSaveNotification(userId, activity, today)
                : updateNotification(existing.getId());
    }

    private Notification createAndSaveNotification(UUID userId, Activity activity, LocalDate today) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setActivityId(activity.getId());
        notification.setTitle(activity.getName());
        notification.setMessage("Event in " + Period.between(today, activity.getStartDate()).getDays() + " days!");
        notification.setDateTime(LocalDateTime.now());

        return notificationRepository.save(notification);
    }

    private void createAndSaveNotificationPreference(UUID userId, UUID activityId) {
        NotificationPreference preference = new NotificationPreference();
        preference.setUserId(userId);
        preference.setActivityId(activityId);
        preference.setActive(true);
        addNotificationPreference(preference);
    }

    public Notification updateNotification(UUID id) {
        Notification notification = getNotificationById(id);
        if (notification == null) return null;

        Activity activity = activityService.getActivityById(notification.getActivityId());
        if (activity == null) return null;

        notification.setDateTime(LocalDateTime.now());
        notification.setMessage("Event in " + Period.between(LocalDate.now(), activity.getStartDate()).getDays() + " days!");
        return notificationRepository.save(notification);
    }

    public void disableAndDeleteNotification(UUID userId, UUID activityId) {
        Optional.ofNullable(notificationPreferenceRepository.findByUserIdAndActivityId(userId, activityId))
                .ifPresent(pref -> {
                    pref.setActive(false);
                    notificationPreferenceRepository.save(pref);
                });

        Optional.ofNullable(notificationRepository.findByUserIdAndActivityId(userId, activityId))
                .ifPresent(notificationRepository::delete);
    }
}
