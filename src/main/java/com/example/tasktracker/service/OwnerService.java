package com.example.tasktracker.service;

import com.example.tasktracker.dto.CreateOwnerRequest;
import com.example.tasktracker.dto.OwnerDTO;
import com.example.tasktracker.model.Owner;
import com.example.tasktracker.repository.OwnerRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OwnerService {

    private final OwnerRepository ownerRepository;

    public OwnerService(OwnerRepository ownerRepository) {
        this.ownerRepository = ownerRepository;
    }

    public OwnerDTO toDTO(Owner owner) {
        return new OwnerDTO(owner.getId(), owner.getFullName(), owner.getPhone(), owner.getEmail());
    }

    @Transactional(readOnly = true)
    public List<OwnerDTO> findAll() {
        return ownerRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    @Transactional
    public OwnerDTO create(CreateOwnerRequest request) {
        Owner owner = new Owner(request.getFullName(), request.getPhone(), request.getEmail());
        return toDTO(ownerRepository.save(owner));
    }
}
