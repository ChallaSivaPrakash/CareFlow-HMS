package com.careflow.hms.repository;

import com.careflow.hms.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    Optional<Patient> findByPatientId(String patientId);
    List<Patient> findByStatus(String status);
    List<Patient> findByAssignedDoctorId(Long doctorId);
    List<Patient> findAllByOrderByCreatedAtDesc();
    long countByStatus(String status);
    
    @Query("SELECT COUNT(p) FROM Patient p WHERE p.assignedDepartment = ?1 AND p.createdAt BETWEEN ?2 AND ?3")
    long countByDepartmentAndDateRange(String department, LocalDateTime start, LocalDateTime end);
}
