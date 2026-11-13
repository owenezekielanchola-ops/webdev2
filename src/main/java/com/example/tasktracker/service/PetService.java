package com.example.tasktracker.service;

import com.example.tasktracker.dto.CreatePetRequest;
import com.example.tasktracker.dto.PetDTO;
import com.example.tasktracker.dto.UpdatePetRequest;
import com.example.tasktracker.exception.DuplicateResourceException;
import com.example.tasktracker.exception.ResourceNotFoundException;
import com.example.tasktracker.model.Owner;
import com.example.tasktracker.model.Pet;
import com.example.tasktracker.repository.OwnerRepository;
import com.example.tasktracker.repository.PetRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    public Page<PetDTO> findAll(int page, int size, String sort, String direction, String species) {
        int cappedSize = Math.min(Math.max(size, 1), 100);
        String sortField = sort;
        String sortDirection = direction;
        if (sort != null && sort.contains(",")) {
            String[] parts = sort.split(",");
            sortField = parts[0];
            if (parts.length > 1 && !parts[1].isBlank()) {
                sortDirection = parts[1];
            }
        }
        Sort.Direction dir = "desc".equalsIgnoreCase(sortDirection) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(Math.max(page, 0), cappedSize, Sort.by(dir, sortField));
        Page<Pet> result = (species != null && !species.isBlank())
                ? petRepository.findBySpeciesIgnoreCase(species, pageable)
                : petRepository.findAll(pageable);
        return result.map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public PetDTO findById(Long id) {
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pet not found with id: " + id));
        return toDTO(pet);
    }

    @Transactional
    public PetDTO create(CreatePetRequest request) {
        if (petRepository.existsByMicrochipNumber(request.getMicrochipNumber())) {
            throw new DuplicateResourceException("Microchip number already exists: " + request.getMicrochipNumber());
        }
        Pet pet = new Pet();
        pet.setMicrochipNumber(request.getMicrochipNumber());
        pet.setName(request.getName());
        pet.setSpecies(request.getSpecies());
        pet.setBreed(request.getBreed());
        if (request.getOwnerId() != null) {
            Owner owner = ownerRepository.findById(request.getOwnerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Owner not found with id: " + request.getOwnerId()));
            pet.setOwner(owner);
        }
        Pet saved = petRepository.save(pet);
        return toDTO(saved);
    }

    @Transactional
    public PetDTO update(Long id, UpdatePetRequest request) {
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pet not found with id: " + id));
        if (request.getMicrochipNumber() == null || request.getMicrochipNumber().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "microchipNumber is required");
        }
        if (request.getName() == null || request.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "name is required");
        }
        if (request.getSpecies() == null || request.getSpecies().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "species is required");
        }
        if (petRepository.existsByMicrochipNumberAndIdNot(request.getMicrochipNumber(), id)) {
            throw new DuplicateResourceException("Microchip number already exists: " + request.getMicrochipNumber());
        }
        pet.setMicrochipNumber(request.getMicrochipNumber());
        pet.setName(request.getName());
        pet.setSpecies(request.getSpecies());
        pet.setBreed(request.getBreed());
        if (request.getOwnerId() != null) {
            Owner owner = ownerRepository.findById(request.getOwnerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Owner not found with id: " + request.getOwnerId()));
            pet.setOwner(owner);
        } else {
            pet.setOwner(null);
        }
        return toDTO(petRepository.save(pet));
    }

    @Transactional
    public PetDTO patch(Long id, UpdatePetRequest request) {
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pet not found with id: " + id));
        if (request.getMicrochipNumber() != null) {
            if (request.getMicrochipNumber().isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "microchipNumber cannot be blank");
            }
            if (petRepository.existsByMicrochipNumberAndIdNot(request.getMicrochipNumber(), id)) {
                throw new DuplicateResourceException("Microchip number already exists: " + request.getMicrochipNumber());
            }
            pet.setMicrochipNumber(request.getMicrochipNumber());
        }
        if (request.getName() != null) {
            if (request.getName().isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "name cannot be blank");
            }
            pet.setName(request.getName());
        }
        if (request.getSpecies() != null) {
            if (request.getSpecies().isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "species cannot be blank");
            }
            pet.setSpecies(request.getSpecies());
        }
        if (request.getBreed() != null) {
            pet.setBreed(request.getBreed());
        }
        if (request.getOwnerId() != null) {
            Owner owner = ownerRepository.findById(request.getOwnerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Owner not found with id: " + request.getOwnerId()));
            pet.setOwner(owner);
        }
        return toDTO(petRepository.save(pet));
    }

    @Transactional
    public void delete(Long id) {
        if (!petRepository.existsById(id)) {
            throw new ResourceNotFoundException("Pet not found with id: " + id);
        }
        petRepository.deleteById(id);
    }
}
