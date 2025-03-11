package com.software_design.horseland.service;

import com.software_design.horseland.model.Activity;
import com.software_design.horseland.repository.ActivityRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ActivityService {
    private final ActivityRepository activityRepository;

    public List<Activity> getActivities() {
        return activityRepository.findAll();
    }

    public Activity addActivity(Activity activity) {
        return activityRepository.save(activity);
    }

    public Activity updateActivity(UUID uuid, Activity activity) {
        Activity existingActivity = activityRepository.findById(uuid).orElseThrow(
                () -> new IllegalStateException("Activity with uuid " + uuid + " not found"));
        existingActivity.setName(activity.getName());
        existingActivity.setDescription(activity.getDescription());
        existingActivity.setStartDate(activity.getStartDate());
        existingActivity.setEndDate(activity.getEndDate());

        return activityRepository.save(existingActivity);
    }

    public void deleteActivity(UUID uuid) {
        activityRepository.deleteById(uuid);
    }

    public Activity getActivityById(UUID uuid) {
        return activityRepository.findById(uuid).orElseThrow(
                () -> new IllegalStateException("Activity with uuid " + uuid + " not found"));
    }

    public Activity getActivityByName(String name) {
        return activityRepository.findByName(name);
    }
}
