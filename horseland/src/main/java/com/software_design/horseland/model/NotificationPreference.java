package com.software_design.horseland.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(
        name = "notification_preference",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "activity_id"})
        }
)
public class NotificationPreference {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private UUID userId;
    private UUID activityId;

    private Boolean active;
}
