package com.example.tasktracker.controller;

import com.example.tasktracker.dto.CreatePetRequest;
import com.example.tasktracker.dto.PetDTO;
import com.example.tasktracker.dto.UpdatePetRequest;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pets")
public class PetController {

    @GetMapping
    public List<PetDTO> getAll() {
        return null;
    }

    @GetMapping("/{id}")
    public ResponseEntity<PetDTO> getById(@PathVariable Long id) {
        return null;
    }

    @PostMapping
    public ResponseEntity<PetDTO> create(@RequestBody CreatePetRequest request) {
        return null;
    }

    @PutMapping("/{id}")
    public ResponseEntity<PetDTO> update(@PathVariable Long id, @RequestBody UpdatePetRequest request) {
        return null;
    }

    @PatchMapping("/{id}")
    public ResponseEntity<PetDTO> patch(@PathVariable Long id, @RequestBody UpdatePetRequest request) {
        return null;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return null;
    }
}
