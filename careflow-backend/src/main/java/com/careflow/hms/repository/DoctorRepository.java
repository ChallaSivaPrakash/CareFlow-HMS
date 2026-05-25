package com.careflow.hms.repository;

import com.careflow.hms.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    List<Doctor> findByIsActiveTrue();
    List<Doctor> findByDepartment(String department);
    Optional<Doctor> findByEmail(String email);
}
