package com.careflow.backend.repository;

import com.careflow.backend.entity.PatientRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PatientRepository extends JpaRepository<PatientRecord, Long> {
    List<PatientRecord> findByStatus(String status);
    List<PatientRecord> findByAssignedDoctor_Id(Long doctorId);
    List<PatientRecord> findByNameContainingIgnoreCase(String name);
    List<PatientRecord> findByPatientIdContainingIgnoreCase(String patientId);
    List<PatientRecord> findByNameContainingIgnoreCaseAndPatientIdContainingIgnoreCase(String name, String patientId);
    long countByStatus(String status);
    long countByAssignedDepartmentAndCreatedAtBetween(String dept, LocalDateTime start, LocalDateTime end);
}
