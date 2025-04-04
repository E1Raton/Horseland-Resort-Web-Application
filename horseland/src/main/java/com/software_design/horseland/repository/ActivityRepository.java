package com.software_design.horseland.repository;

import com.software_design.horseland.model.Activity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ActivityRepository extends JpaRepository<Activity, UUID> {
    Optional<Activity> findByName(String name);

    List<Activity> findAll(Sort sort);

    List<Activity> findByNameContainingIgnoreCase(String search);

    List<Activity> findByStartDateAfter(LocalDate date);
}
