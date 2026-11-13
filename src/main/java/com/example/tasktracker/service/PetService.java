package com.example.tasktracker.service;

import com.example.tasktracker.dto.CreatePetRequest;
import com.example.tasktracker.dto.PetDTO;
import com.example.tasktracker.dto.UpdatePetRequest;
import com.example.tasktracker.model.Owner;
import com.example.tasktracker.model.Pet;
import com.example.tasktracker.repository.OwnerRepository;
import com.example.tasktracker.repository.PetRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PetService {

    private final PetRepository petRepository;
    private final OwnerRepository ownerRepository;

    public PetService(PetRepository petRepository, OwnerRepository ownerRepository) {
        this.petRepository = petRepository;
        this.ownerRepository = ownerRepository;
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

    @Transactional
    public PetDTO create(CreatePetRequest request) {
        Pet pet = new Pet();
        pet.setMicrochipNumber(request.getMicrochipNumber());
        pet.setName(request.getName());
        pet.setSpecies(request.getSpecies());
        pet.setBreed(request.getBreed());
        if (request.getOwnerId() != null) {
            Owner owner = ownerRepository.findById(request.getOwnerId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Owner not found with id: " + request.getOwnerId()));
            pet.setOwner(owner);
        }
        Pet saved = petRepository.save(pet);
        return toDTO(saved);
    }

    @Transactional
    public Optional<PetDTO> update(Long id, UpdatePetRequest request) {
        return petRepository.findById(id).map(pet -> {
            if (request.getMicrochipNumber() == null || request.getMicrochipNumber().isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "microchipNumber is required");
            }
            pet.setMicrochipNumber(request.getMicrochipNumber());
            pet.setName(request.getName());
            pet.setSpecies(request.getSpecies());
            pet.setBreed(request.getBreed());
            if (request.getOwnerId() != null) {
                Owner owner = ownerRepository.findById(request.getOwnerId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Owner not found with id: " + request.getOwnerId()));
                pet.setOwner(owner);
            } else {
                pet.setOwner(null);
            }
            return toDTO(petRepository.save(pet));
        });
    }

    @Transactional
    public Optional<PetDTO> patch(Long id, UpdatePetRequest request) {
        return petRepository.findById(id).map(pet -> {
            if (request.getMicrochipNumber() != null) {
                if (request.getMicrochipNumber().isBlank()) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "microchipNumber cannot be blank");
                }
                pet.setMicrochipNumber(request.getMicrochipNumber());
            }
            if (request.getName() != null) {
                pet.setName(request.getName());
            }
            if (request.getSpecies() != null) {
                pet.setSpecies(request.getSpecies());
            }
            if (request.getBreed() != null) {
                pet.setBreed(request.getBreed());
            }
            if (request.getOwnerId() != null) {
                Owner owner = ownerRepository.findById(request.getOwnerId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Owner not found with id: " + request.getOwnerId()));
                pet.setOwner(owner);
            }
            return toDTO(petRepository.save(pet));
        });
    }

    @Transactional
    public boolean delete(Long id) {
        if (!petRepository.existsById(id)) {
            return false;
        }
        petRepository.deleteById(id);
        return true;
    }
}
