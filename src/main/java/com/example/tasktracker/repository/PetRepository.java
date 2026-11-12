package com.example.tasktracker.repository;

import com.example.tasktracker.model.Pet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PetRepository extends JpaRepository<Pet, Long> {
}
