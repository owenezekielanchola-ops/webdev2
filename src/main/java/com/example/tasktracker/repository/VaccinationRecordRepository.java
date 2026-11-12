package com.example.tasktracker.repository;

import com.example.tasktracker.model.VaccinationRecord;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VaccinationRecordRepository extends JpaRepository<VaccinationRecord, Long> {
    List<VaccinationRecord> findByPetId(Long petId);
}
