package com.software_design.horseland.service;

import com.software_design.horseland.exception.DatabaseValidationException;
import com.software_design.horseland.model.Activity;
import com.software_design.horseland.model.ActivityDTO;
import com.software_design.horseland.model.User;
import com.software_design.horseland.repository.ActivityRepository;
import com.software_design.horseland.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ActivityService {
    private final ActivityRepository activityRepository;
    private final UserRepository userRepository;

    public List<Activity> getActivities() {
        return activityRepository.findAll();
    }

    public List<Activity> getSortedActivities(Sort sort) {
        return activityRepository.findAll(sort);
    }

    public List<Activity> searchByName(String search) {
        return activityRepository.findByNameContainingIgnoreCase(search);
    }

    public List<Activity> getFutureActivities() {
        return activityRepository.findByStartDateAfter(LocalDate.now());
    }

    public List<Activity> getByParticipantId(UUID participantId) {
        return getActivities().stream()
                .filter(activity -> activity.getParticipants().stream()
                        .anyMatch(participant -> participant.getId().equals(participantId)))
                .collect(Collectors.toList());
    }

    public Activity addActivity(ActivityDTO activityDTO) throws DatabaseValidationException {

        validateActivityDoesNotExist(activityDTO.getName());

        Activity activity = new Activity();
        setActivityFields(activity, activityDTO);

        return activityRepository.save(activity);
    }

    private void validateActivityDoesNotExist(String name) throws DatabaseValidationException {
        if (getActivityByName(name) != null) {
            throw new DatabaseValidationException("Activity with the same name found");
        }
    }

    private void setActivityFields(Activity activity, ActivityDTO activityDTO) {
        activity.setName(activityDTO.getName());
        activity.setDescription(activityDTO.getDescription());
        activity.setStartDate(activityDTO.getStartDate());
        activity.setEndDate(activityDTO.getEndDate());
    }

    public Activity updateActivity(UUID uuid, ActivityDTO activityDTO) throws DatabaseValidationException {
        Activity existingActivity = getActivityById(uuid);

        Optional<Activity> activityByName = activityRepository.findByName(activityDTO.getName());
        if (activityByName.isPresent() && !uuid.equals(activityByName.get().getId())) {
            throw new DatabaseValidationException("Activity with the same name found");
        }

        setActivityFields(existingActivity, activityDTO);

        return activityRepository.save(existingActivity);
    }

    public Activity addParticipantToActivity(UUID uuid, UUID participantId) throws DatabaseValidationException {
        Activity activity = getActivityById(uuid); // Throws exception if not found
        User user = userRepository.findById(participantId)
                .orElseThrow(() -> new DatabaseValidationException("User with uuid " + participantId + " not found"));

        if (activity.getParticipants().contains(user)) {
            System.out.println("Here!");
            throw new DatabaseValidationException("User is already registered for this activity");
        }

        activity.getParticipants().add(user);
        return activityRepository.save(activity);
    }

    public Activity removeParticipantFromActivity(UUID activityId, UUID participantId) throws DatabaseValidationException {
        Activity activity = getActivityById(activityId);

        // Check if user is a participant
        Optional<User> user = userRepository.findById(participantId);
        if (user.isPresent() && activity.getParticipants().contains(user.get())) {

            activity.getParticipants().remove(user.get());
            return activityRepository.save(activity);
        } else {
            throw new DatabaseValidationException("User not registered for this activity.");
        }
    }

    public void deleteActivity(UUID uuid) {
        activityRepository.deleteById(uuid);
    }

    public Activity getActivityById(UUID uuid) {
        return activityRepository.findById(uuid).orElse(null);
    }

    public Activity getActivityByName(String name) {
        return activityRepository.findByName(name).orElse(null);
    }
}
