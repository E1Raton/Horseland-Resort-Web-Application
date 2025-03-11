package com.software_design.horseland.repository;

import com.software_design.horseland.model.Horse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface HorseRepository extends JpaRepository<Horse, UUID> {
    List<Horse> findByOwnerId(UUID ownerId);
}
