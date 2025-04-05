package com.software_design.horseland.events.listeners;

import com.software_design.horseland.events.UserLoginEvent;
import com.software_design.horseland.model.User;
import com.software_design.horseland.service.NotificationService;
import lombok.AllArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class UserEventListener {

    private final NotificationService notificationService;

    @Async
    @EventListener
    public void handleUserLogin(UserLoginEvent event) {
        User user = event.getUser();
        notificationService.addNotifications(user.getId());
    }
}
