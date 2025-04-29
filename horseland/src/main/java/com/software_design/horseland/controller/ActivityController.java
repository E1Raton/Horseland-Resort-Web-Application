package com.software_design.horseland.controller;

import com.software_design.horseland.exception.DatabaseValidationException;
import com.software_design.horseland.model.Activity;
import com.software_design.horseland.model.ActivityDTO;
import com.software_design.horseland.service.ActivityService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@CrossOrigin
public class ActivityController {
    private final ActivityService activityService;

    @GetMapping("/activity")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR', 'STUDENT')")
    public List<Activity> getAllActivities() {
        return activityService.getActivities();
    }

    @GetMapping("/activity/{uuid}")
    @PreAuthorize("hasRole('ADMIN')")
    public Activity getActivityById(@PathVariable UUID uuid) {
        return activityService.getActivityById(uuid);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/activity/name/{name}")
    public Activity getActivityByName(@PathVariable String name) {
        return activityService.getActivityByName(name);
    }

    @GetMapping("/activity/participant/{participantId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR', 'STUDENT')")
    public List<Activity> getActivityByParticipantId(@PathVariable UUID participantId) {
        return activityService.getByParticipantId(participantId);
    }

    @GetMapping("/activity/ordered/{order}")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Activity> getActivityByOrder(@PathVariable String order) {
        return switch (order) {
            case "date" -> activityService.getSortedActivities(Sort.by(Sort.Direction.DESC, "startDate"));
            case "name" -> activityService.getSortedActivities(Sort.by(Sort.Direction.ASC, "name"));
            default -> getAllActivities();
        };
    }

    @GetMapping("/activity/search/name/{search}")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Activity> searchActivityByName(@PathVariable String search) {
        return activityService.searchByName(search);
    }

    @GetMapping("/activity/future")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR', 'STUDENT')")
    public List<Activity> getFutureActivities() {
        return activityService.getFutureActivities();
    }

    @PostMapping("/activity")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public Activity addActivity(@Valid @RequestBody ActivityDTO activityDTO) throws DatabaseValidationException {
        return activityService.addActivity(activityDTO);
    }

    @PutMapping("/activity/{uuid}")
    @PreAuthorize("hasRole('ADMIN')")
    public Activity updateActivity(@PathVariable UUID uuid, @Valid @RequestBody ActivityDTO activityDTO) throws DatabaseValidationException {
        return activityService.updateActivity(uuid, activityDTO);
    }

    @PutMapping("/activity/{uuid}/register/{participantId}")
    @PreAuthorize("#participantId.toString() == authentication.details")
    public Activity addParticipantToActivity(@PathVariable UUID uuid, @PathVariable UUID participantId) throws DatabaseValidationException {
        return activityService.addParticipantToActivity(uuid, participantId);
    }

    @PutMapping("/activity/{uuid}/deregister/{participantId}")
    @PreAuthorize("#participantId.toString() == authentication.details")
    public Activity removeParticipantFromActivity(@PathVariable UUID uuid, @PathVariable UUID participantId) throws DatabaseValidationException {
        return activityService.removeParticipantFromActivity(uuid, participantId);
    }

    @DeleteMapping("/activity/{uuid}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteActivity(@PathVariable UUID uuid) {
        activityService.deleteActivity(uuid);
    }
}
