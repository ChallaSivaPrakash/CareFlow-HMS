package com.careflow.backend.repository;

import com.careflow.backend.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    List<Doctor> findByIsActiveTrue();
    List<Doctor> findByDepartment(String department);
    List<Doctor> findBySpecialty(String specialty);
}
