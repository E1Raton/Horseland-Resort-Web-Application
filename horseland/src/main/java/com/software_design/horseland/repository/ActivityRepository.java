package com.software_design.horseland.repository;

import com.software_design.horseland.model.Activity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ActivityRepository extends JpaRepository<Activity, UUID> {
    public Optional<Activity> findByName(String name);
}
