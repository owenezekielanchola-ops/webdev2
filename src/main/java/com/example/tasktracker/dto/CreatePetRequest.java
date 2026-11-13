package com.example.tasktracker.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreatePetRequest {

    @NotBlank(message = "Microchip number is required")
    @Size(min = 3, max = 50, message = "Microchip number must be between 3 and 50 characters")
    private String microchipNumber;

    @NotBlank(message = "Pet name is required")
    @Size(max = 100, message = "Pet name must be at most 100 characters")
    private String name;

    @NotBlank(message = "Species is required")
    @Size(max = 50, message = "Species must be at most 50 characters")
    private String species;

    @Size(max = 80, message = "Breed must be at most 80 characters")
    private String breed;

    @Min(value = 1, message = "Owner ID must be at least 1")
    private Long ownerId;

    public CreatePetRequest() {
    }

    public String getMicrochipNumber() {
        return microchipNumber;
    }

    public void setMicrochipNumber(String microchipNumber) {
        this.microchipNumber = microchipNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }
}
