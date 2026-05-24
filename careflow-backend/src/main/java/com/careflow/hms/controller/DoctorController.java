package com.careflow.hms.controller;

import com.careflow.hms.entity.Doctor;
import com.careflow.hms.repository.DoctorRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/doctors")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class DoctorController {

    private final DoctorRepository doctorRepository;

    public DoctorController(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    @GetMapping
    public ResponseEntity<List<Doctor>> getAllDoctors() {
        return ResponseEntity.ok(doctorRepository.findAll());
    }

    @GetMapping("/active")
    public ResponseEntity<List<Doctor>> getActiveDoctors() {
        return ResponseEntity.ok(doctorRepository.findByIsActiveTrue());
    }

    @PostMapping
    public ResponseEntity<Doctor> createDoctor(@RequestBody Doctor doctor) {
        return ResponseEntity.ok(doctorRepository.save(doctor));
    }
}
