package com.example.tasktracker.repository;

import com.example.tasktracker.model.Pet;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PetRepository extends JpaRepository<Pet, Long> {

    List<Pet> findBySpeciesIgnoreCase(String species);

    Page<Pet> findBySpeciesIgnoreCase(String species, Pageable pageable);

    List<Pet> findByNameContainingIgnoreCase(String keyword);

    @Query("SELECT DISTINCT p.species FROM Pet p")
    List<String> findAllSpecies();

    boolean existsByMicrochipNumber(String microchipNumber);

    boolean existsByMicrochipNumberAndIdNot(String microchipNumber, Long id);
}
