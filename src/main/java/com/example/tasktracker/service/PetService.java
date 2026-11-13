package com.example.tasktracker.service;

import com.example.tasktracker.dto.PetDTO;
import com.example.tasktracker.model.Pet;
import com.example.tasktracker.repository.PetRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PetService {

    private final PetRepository petRepository;

    public PetService(PetRepository petRepository) {
        this.petRepository = petRepository;
    }

    public PetDTO toDTO(Pet pet) {
        Long ownerId = pet.getOwner() != null ? pet.getOwner().getId() : null;
        return new PetDTO(
                pet.getId(),
                pet.getMicrochipNumber(),
                pet.getName(),
                pet.getSpecies(),
                pet.getBreed(),
                ownerId
        );
    }

    @Transactional(readOnly = true)
    public List<PetDTO> findAll() {
        return petRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public Optional<PetDTO> findById(Long id) {
        return petRepository.findById(id)
                .map(this::toDTO);
    }
}
