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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

        List<Activity> activities = activityService.getByParticipantId(userId);
        List<Activity> upcomingActivities = activities.stream()
                .filter(a -> Math.abs(Period.between(LocalDate.now(), a.getStartDate()).getDays()) < DAYS_INTERVAL)
                .toList();

        List<Notification> result = new ArrayList<>();

        for (Activity activity : upcomingActivities) {
            NotificationPreference notificationPreference = notificationPreferenceRepository.findByUserIdAndActivityId(userId, activity.getId());
            if (notificationPreference != null) {
                if (notificationPreference.getActive()) {
                    Notification notification = notificationRepository.findByUserIdAndActivityId(userId, activity.getId());
                    if (notification == null) {
                        notification = new Notification();
                        notification.setUserId(userId);
                        notification.setActivityId(activity.getId());
                        notification.setTitle(activity.getName());
                        notification.setMessage("Event in " + Period.between(LocalDate.now(), activity.getStartDate()).getDays() + " days!");
                        notification.setDateTime(LocalDateTime.now());
                        result.add(notificationRepository.save(notification));
                    }
                    else {
                        result.remove(notification);
                        Notification updatedNot = updateNotification(notification.getId());
                        result.add(updatedNot);
                    }
                }
            }
            else {
                NotificationPreference newNotificationPreference = new NotificationPreference();
                newNotificationPreference.setUserId(userId);
                newNotificationPreference.setActivityId(activity.getId());
                newNotificationPreference.setActive(true);
                addNotificationPreference(newNotificationPreference);

                Notification notification = new Notification();
                notification.setUserId(userId);
                notification.setActivityId(activity.getId());
                notification.setTitle(activity.getName());
                notification.setMessage("Event in " + Period.between(LocalDate.now(), activity.getStartDate()).getDays() + " days!");
                notification.setDateTime(LocalDateTime.now());
                result.add(addNotification(notification));
            }
        }

        return result;
    }

    public Notification updateNotification(UUID id) {
        Notification notification = getNotificationById(id);
        if (notification != null) {
            Activity activity = activityService.getActivityById(notification.getActivityId());

            if (activity != null) {
                notification.setDateTime(LocalDateTime.now());
                notification.setMessage("Event in " + Period.between(LocalDate.now(), activity.getStartDate()).getDays() + " days!");

                return notificationRepository.save(notification);
            }
        }

        return null;
    }

    public void disableAndDeleteNotification(UUID userId, UUID activityId) {
        NotificationPreference notificationPreference = notificationPreferenceRepository.findByUserIdAndActivityId(userId, activityId);
        if (notificationPreference != null) {
            notificationPreference.setActive(false);
            notificationPreferenceRepository.save(notificationPreference);
        }

        Notification notification = notificationRepository.findByUserIdAndActivityId(userId, activityId);
        if (notification != null) {
            notificationRepository.delete(notification);
        }
    }
}
