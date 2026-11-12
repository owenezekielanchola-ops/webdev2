package com.example.tasktracker.service;

import com.example.tasktracker.model.Pet;
import com.example.tasktracker.repository.PetRepository;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class PetDataSeeder implements CommandLineRunner {

    private final PetRepository petRepository;

    public PetDataSeeder(PetRepository petRepository) {
        this.petRepository = petRepository;
    }

    @Override
    public void run(String... args) {
        if (petRepository.count() == 0) {
            petRepository.save(new Pet("MC-1001", "Buddy", "Dog", "Golden Retriever"));
            petRepository.save(new Pet("MC-1002", "Luna", "Cat", "Persian"));
            petRepository.save(new Pet("MC-1003", "Max", "Dog", "German Shepherd"));
        }

        List<Pet> pets = petRepository.findAll();
        System.out.println("Seeded pets count: " + pets.size());
        for (Pet p : pets) {
            System.out.println(p.getId() + " | " + p.getMicrochipNumber() + " | " + p.getName() + " | " + p.getSpecies() + " | " + p.getBreed());
        }
    }
}
