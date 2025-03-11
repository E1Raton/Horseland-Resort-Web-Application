package com.software_design.horseland.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Data
@Table(name = "horse")
public class Horse {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private LocalDate birthDate;

    @Column(nullable = false)
    private HorseBreed breed;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;
}
