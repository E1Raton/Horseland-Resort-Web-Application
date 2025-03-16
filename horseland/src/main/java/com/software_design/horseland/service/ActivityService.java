package com.software_design.horseland.service;

import com.software_design.horseland.exception.DatabaseValidationException;
import com.software_design.horseland.model.Activity;
import com.software_design.horseland.model.ActivityDTO;
import com.software_design.horseland.model.User;
import com.software_design.horseland.repository.ActivityRepository;
import com.software_design.horseland.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@AllArgsConstructor
public class ActivityService {
    private final ActivityRepository activityRepository;
    private final UserRepository userRepository;

    public List<Activity> getActivities() {
        return activityRepository.findAll();
    }

    public Activity addActivity(ActivityDTO activityDTO) throws DatabaseValidationException {

        Activity activity = new Activity();

        Activity activityByName = getActivityByName(activityDTO.getName());
        if (activityByName != null) {
            throw new DatabaseValidationException("Activity with the same name found");
        }

        Set<User> participants = new HashSet<>();
        if (activityDTO.getParticipantsIds() != null) {
            for (UUID uuid : activityDTO.getParticipantsIds()) {
                Optional<User> participant = userRepository.findById(uuid);

                if (participant.isPresent()) {
                    participants.add(participant.get());
                }
                else {
                    throw new DatabaseValidationException("User with uuid " + uuid + " not found");
                }
            }
        }

        activity.setName(activityDTO.getName());
        activity.setStartDate(activityDTO.getStartDate());
        activity.setEndDate(activityDTO.getEndDate());
        activity.setParticipants(participants);

        return activityRepository.save(activity);
    }

    public Activity updateActivity(UUID uuid, ActivityDTO activityDTO) throws DatabaseValidationException {
        Activity existingActivity = getActivityById(uuid);

        Activity activityByName = getActivityByName(activityDTO.getName());
        if (activityByName != null && !uuid.equals(activityByName.getId())) {
            throw new DatabaseValidationException("Activity with the same name found");
        }

        existingActivity.setName(activityDTO.getName());
        existingActivity.setDescription(activityDTO.getDescription());
        existingActivity.setStartDate(activityDTO.getStartDate());
        existingActivity.setEndDate(activityDTO.getEndDate());

        Set<User> participants = new HashSet<>();
        if (activityDTO.getParticipantsIds() != null) {
            for (UUID participantId : activityDTO.getParticipantsIds()) {
                Optional<User> participant = userRepository.findById(participantId);
                if (participant.isPresent()) {
                    participants.add(participant.get());
                }
                else {
                    throw new DatabaseValidationException("User with uuid " + participantId + " not found");
                }
            }
        }

        existingActivity.setParticipants(participants);

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
        Optional<Activity> activityByName = activityRepository.findByName(name);
        return activityByName.orElse(null);
    }
}
