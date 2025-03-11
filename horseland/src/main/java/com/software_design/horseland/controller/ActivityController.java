package com.software_design.horseland.controller;

import com.software_design.horseland.model.Activity;
import com.software_design.horseland.service.ActivityService;
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
    public Activity addActivity(@RequestBody Activity activity) {
        return activityService.addActivity(activity);
    }

    @PutMapping("/activity/{uuid}")
    public Activity updateActivity(@PathVariable UUID uuid, @RequestBody Activity activity) {
        return activityService.updateActivity(uuid, activity);
    }

    @DeleteMapping("/activity/{uuid}")
    public void deleteActivity(@PathVariable UUID uuid) {
        activityService.deleteActivity(uuid);
    }
}
