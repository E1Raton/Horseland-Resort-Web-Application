package com.software_design.horseland.service;

import com.software_design.horseland.model.*;
import com.software_design.horseland.repository.ActivityRepository;
import com.software_design.horseland.repository.NotificationPreferenceRepository;
import com.software_design.horseland.repository.NotificationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTests {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private NotificationPreferenceRepository notificationPreferenceRepository;

    @Mock
    private ActivityService activityService;

    @InjectMocks
    private NotificationService notificationService;

    @Test
    void testAddNotifications_ShouldAdd_WhenNotificationActiveAndNoNotifications() {
        // given:
        // user participant
        User user = new User();
        user.setId(UUID.randomUUID());

        // activity in 5 days
        Activity a1 = new Activity();
        UUID activityId = UUID.randomUUID();
        a1.setId(activityId);
        a1.setName("Activity 1");
        a1.setStartDate(LocalDate.now().plusDays(5));
        a1.addParticipant(user);

        // notification preference
        NotificationPreference pref = new NotificationPreference();
        pref.setId(UUID.randomUUID());
        pref.setUserId(user.getId());
        pref.setActivityId(activityId);
        pref.setActive(true);

        // notification
        Notification notification = new Notification();
        notification.setId(UUID.randomUUID());
        notification.setUserId(user.getId());
        notification.setActivityId(activityId);
        notification.setTitle("Activity 1");
        notification.setMessage("Event in 5 days!");
        notification.setDateTime(LocalDateTime.now());

        // when:
        when(activityService.getByParticipantId(user.getId())).thenReturn(List.of(a1));
        when(notificationPreferenceRepository.findByUserIdAndActivityId(user.getId(), a1.getId()))
                .thenReturn(pref);
        when(notificationRepository.findByUserIdAndActivityId(user.getId(), a1.getId()))
                .thenReturn(null);
        when(notificationRepository.save(any())).thenReturn(notification);
        List<Notification> result = notificationService.addNotifications(user.getId());

        // then:
        assertEquals(1, result.size());
        assertEquals("Activity 1", result.getFirst().getTitle());
        assertEquals("Event in 5 days!", result.getFirst().getMessage());

        verify(activityService, times(1)).getByParticipantId(user.getId());
        verify(notificationPreferenceRepository, times(1)).findByUserIdAndActivityId(user.getId(), a1.getId());
        verify(notificationRepository, times(1)).findByUserIdAndActivityId(user.getId(), a1.getId());
        verify(notificationRepository, times(1)).save(any());
    }

    @Test
    void testAddNotifications_ShouldUpdate_WhenNotificationActiveAndNotificationExists() {
        // given:
        User user = new User();
        user.setId(UUID.randomUUID());

        // Set up activity and its related data
        Activity a1 = new Activity();
        UUID activityId = UUID.randomUUID();
        a1.setId(activityId);
        a1.setName("Sample Activity");
        a1.setStartDate(LocalDate.now().plusDays(5));  // Activity starts in 5 days
        a1.addParticipant(user);

        // Notification preference for the user and activity
        NotificationPreference pref = new NotificationPreference();
        pref.setId(UUID.randomUUID());
        pref.setUserId(user.getId());
        pref.setActivityId(activityId);
        pref.setActive(true);

        // Existing notification that needs to be updated
        Notification oldNotification = new Notification();
        oldNotification.setId(UUID.randomUUID());
        oldNotification.setUserId(user.getId());
        oldNotification.setActivityId(activityId);
        oldNotification.setTitle("Sample Activity");
        oldNotification.setMessage("Event in 7 days!");
        oldNotification.setDateTime(LocalDateTime.now().minusDays(1));

        // Updated notification
        Notification updatedNotification = new Notification();
        updatedNotification.setId(oldNotification.getId());
        updatedNotification.setUserId(user.getId());
        updatedNotification.setActivityId(activityId);
        updatedNotification.setTitle("Sample Activity");
        updatedNotification.setMessage("Event in 5 days!");
        updatedNotification.setDateTime(LocalDateTime.now());

        // when:
        when(activityService.getByParticipantId(user.getId())).thenReturn(List.of(a1));  // Returns the list of activities
        when(notificationPreferenceRepository.findByUserIdAndActivityId(user.getId(), activityId)).thenReturn(pref);
        when(notificationRepository.findByUserIdAndActivityId(user.getId(), activityId)).thenReturn(oldNotification);
        when(notificationRepository.save(any())).thenReturn(updatedNotification);
        when(notificationRepository.findById(any())).thenReturn(Optional.of(oldNotification));  // Mocking findById to return the existing notification
        when(activityService.getActivityById(any())).thenReturn(a1);  // Ensure this returns an Activity, not Notification

        // Method call being tested
        List<Notification> result = notificationService.addNotifications(user.getId());

        // then:
        assertEquals(1, result.size());
        assertEquals("Sample Activity", result.getFirst().getTitle());
        assertEquals("Event in 5 days!", result.getFirst().getMessage());
        assertNotNull(result.getFirst().getDateTime());

        verify(activityService, times(1)).getByParticipantId(user.getId());
        verify(notificationPreferenceRepository, times(1)).findByUserIdAndActivityId(user.getId(), activityId);
        verify(notificationRepository, times(1)).save(any());
        verify(notificationRepository, times(1)).findByUserIdAndActivityId(user.getId(), activityId);
    }

    @Test
    void testAddNotifications_ShouldNotAdd_WhenNotificationNotActive() {
        // given:
        User user = new User();
        user.setId(UUID.randomUUID());

        Activity a1 = new Activity();
        UUID activityId = UUID.randomUUID();
        a1.setId(activityId);
        a1.setName("Activity 1");
        a1.setStartDate(LocalDate.now().plusDays(5));
        a1.addParticipant(user);

        NotificationPreference pref = new NotificationPreference();
        pref.setId(UUID.randomUUID());
        pref.setUserId(user.getId());
        pref.setActivityId(activityId);
        pref.setActive(false);

        // when:
        when(activityService.getByParticipantId(user.getId())).thenReturn(List.of(a1));
        when(notificationPreferenceRepository.findByUserIdAndActivityId(user.getId(), a1.getId()))
                .thenReturn(pref);

        List<Notification> result = notificationService.addNotifications(user.getId());

        // then:
        assertEquals(0, result.size());
    }

    @Test
    void testAddNotifications_ShouldAdd_WhenNoNotificationAndNoPreference() {
        // given:
        User user = new User();
        user.setId(UUID.randomUUID());

        Activity a1 = new Activity();
        UUID activityId = UUID.randomUUID();
        a1.setId(activityId);
        a1.setName("Activity 1");
        a1.setStartDate(LocalDate.now().plusDays(5));
        a1.addParticipant(user);

        NotificationPreference pref = new NotificationPreference();
        pref.setId(UUID.randomUUID());
        pref.setUserId(user.getId());
        pref.setActivityId(activityId);
        pref.setActive(true);

        Notification notification = new Notification();
        notification.setId(UUID.randomUUID());
        notification.setTitle("Activity 1");
        notification.setMessage("Event in 5 days!");
        notification.setDateTime(LocalDateTime.now());

        // when:
        when(activityService.getByParticipantId(user.getId())).thenReturn(List.of(a1));
        when(notificationPreferenceRepository.findByUserIdAndActivityId(user.getId(), a1.getId()))
                .thenReturn(null);
        when(notificationService.addNotificationPreference(any())).thenReturn(pref);
        when(notificationService.addNotification(any())).thenReturn(notification);

        List<Notification> result = notificationService.addNotifications(user.getId());

        assertEquals(1, result.size());
        assertEquals("Activity 1", result.getFirst().getTitle());
        assertEquals("Event in 5 days!", result.getFirst().getMessage());
    }
}

