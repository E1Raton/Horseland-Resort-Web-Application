package com.software_design.horseland.controller;

import com.software_design.horseland.exception.DatabaseValidationException;
import com.software_design.horseland.model.Activity;
import com.software_design.horseland.model.ActivityDTO;
import com.software_design.horseland.service.ActivityService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@CrossOrigin
public class ActivityController {
    private final ActivityService activityService;

    @GetMapping("/activity")
    public List<Activity> getAllActivities() {
        return activityService.getActivities();
    }

    @GetMapping("/activity/{uuid}")
    public Activity getActivityById(@PathVariable UUID uuid) {
        return activityService.getActivityById(uuid);
    }

    @GetMapping("/activity/name/{name}")
    public Activity getActivityByName(@PathVariable String name) {
        return activityService.getActivityByName(name);
    }

    @PostMapping("/activity")
    public Activity addActivity(@Valid @RequestBody ActivityDTO activityDTO) throws DatabaseValidationException {
        return activityService.addActivity(activityDTO);
    }

    @PutMapping("/activity/{uuid}")
    public Activity updateActivity(@PathVariable UUID uuid, @Valid @RequestBody ActivityDTO activityDTO) throws DatabaseValidationException {
        return activityService.updateActivity(uuid, activityDTO);
    }

    @PutMapping("/activity/{uuid}/register/{participantId}")
    public Activity addParticipantToActivity(@PathVariable UUID uuid, @PathVariable UUID participantId) throws DatabaseValidationException {
        return activityService.addParticipantToActivity(uuid, participantId);
    }

    @PutMapping("/activity/{uuid}/deregister/{participantId}")
    public Activity removeParticipantFromActivity(@PathVariable UUID uuid, @PathVariable UUID participantId) throws DatabaseValidationException {
        return activityService.removeParticipantFromActivity(uuid, participantId);
    }

    @DeleteMapping("/activity/{uuid}")
    public void deleteActivity(@PathVariable UUID uuid) {
        activityService.deleteActivity(uuid);
    }
}
