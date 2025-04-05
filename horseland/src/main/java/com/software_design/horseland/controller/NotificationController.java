package com.software_design.horseland.controller;

import com.software_design.horseland.model.Notification;
import com.software_design.horseland.service.NotificationService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@CrossOrigin
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/notification/{userId}")
    public List<Notification> getNotificationsByUserId(@PathVariable UUID userId) {
        return notificationService.getNotificationsByUserId(userId);
    }

    @DeleteMapping("/notification/{userId}/{activityId}")
    public void deleteNotification(@PathVariable UUID userId, @PathVariable UUID activityId) {
        notificationService.disableAndDeleteNotification(userId, activityId);
    }
}
