package com.careflow.backend.service;

import com.careflow.backend.entity.Doctor;
import com.careflow.backend.entity.User;
import com.careflow.backend.repository.DoctorRepository;
import com.careflow.backend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DoctorService(DoctorRepository doctorRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.doctorRepository = doctorRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Doctor> getAllDoctors() {
        return doctorRepository.findByIsActiveTrue();
    }

    public Optional<Doctor> getDoctorById(Long id) {
        return doctorRepository.findById(id);
    }

    public Doctor saveDoctor(Doctor doctor) {
        // If this is a brand NEW doctor being registered by the Admin...
        if (doctor.getId() == null) {
            // 1. Generate a username (remove spaces and make lowercase, e.g., "Dr. Smith" -> "dr.smith")
            String generatedUsername = doctor.getName().replaceAll("\\s+", "").toLowerCase();
            // 2. Set a temporary default password
            String defaultPassword = "Doctor@2026"; 

            // 3. Create the User login account
            User newDoctorLogin = User.builder()
                    .username(generatedUsername)
                    .password(passwordEncoder.encode(defaultPassword))
                    .role("ROLE_DOCTOR")
                    .name(doctor.getName())
                    .build();
            
            userRepository.save(newDoctorLogin);
            System.out.println("🚨 NEW DOCTOR ACCOUNT CREATED! Username: " + generatedUsername + " | Password: " + defaultPassword);
        }
        
        return doctorRepository.save(doctor);
    }

    public void deleteDoctor(Long id) {
        doctorRepository.findById(id).ifPresent(doctor -> {
            doctor.setActive(false);
            doctorRepository.save(doctor);
            
            // Also deactivate the associated user if it exists
            String generatedUsername = doctor.getName().replaceAll("\\s+", "").toLowerCase();
            userRepository.findByUsername(generatedUsername).ifPresent(user -> {
                user.setActive(false);
                userRepository.save(user);
                System.out.println("🚨 DOCTOR ACCOUNT DEACTIVATED! Username: " + generatedUsername);
            });
        });
    }
}