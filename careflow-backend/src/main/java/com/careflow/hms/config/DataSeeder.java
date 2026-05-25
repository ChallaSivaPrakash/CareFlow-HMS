package com.careflow.hms.config;

import com.careflow.hms.entity.User;
import com.careflow.hms.entity.Doctor;
import com.careflow.hms.repository.UserRepository;
import com.careflow.hms.repository.DoctorRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository, DoctorRepository doctorRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.doctorRepository = doctorRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // Seed Admin / Doctor Account mapped to your real email for OTP testing
        if (!userRepository.existsByUsername("challasivaprakash@gmail.com")) {
            User doctorUser = new User();
            doctorUser.setUsername("challasivaprakash@gmail.com");
            doctorUser.setPassword(passwordEncoder.encode("Doctor@2026"));
            doctorUser.setRole("ROLE_DOCTOR");
            doctorUser.setName("Dr. Sravan");
            doctorUser.setCreatedAt(LocalDateTime.now());
            doctorUser.setUpdatedAt(LocalDateTime.now());
            userRepository.save(doctorUser);

            Doctor doctorProfile = new Doctor();
            doctorProfile.setName("Dr. Sravan");
            doctorProfile.setSpecialty("Cardiology");
            doctorProfile.setDepartment("Cardiology");
            doctorProfile.setContactNumber("9876543210");
            doctorRepository.save(doctorProfile);
            
            System.out.println("✅ Seeded Doctor: challasivaprakash@gmail.com / Doctor@2026");
        }

        // Seed Clerk
        if (!userRepository.existsByUsername("clerk")) {
            User clerkUser = new User();
            clerkUser.setUsername("clerk");
            clerkUser.setPassword(passwordEncoder.encode("Clerk@321"));
            clerkUser.setRole("ROLE_OPD_CLERK");
            clerkUser.setName("Front Desk Clerk");
            clerkUser.setCreatedAt(LocalDateTime.now());
            clerkUser.setUpdatedAt(LocalDateTime.now());
            userRepository.save(clerkUser);
            
            System.out.println("✅ Seeded Clerk: clerk / Clerk@321");
        }
    }
}