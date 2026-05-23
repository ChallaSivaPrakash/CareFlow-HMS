package com.careflow.backend.config;

import com.careflow.backend.entity.Bed;
import com.careflow.backend.entity.Doctor;
import com.careflow.backend.entity.PatientRecord;
import com.careflow.backend.entity.User;
import com.careflow.backend.repository.BedRepository;
import com.careflow.backend.repository.DoctorRepository;
import com.careflow.backend.repository.PatientRepository;
import com.careflow.backend.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final BedRepository bedRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository, 
                      BedRepository bedRepository, 
                      DoctorRepository doctorRepository,
                      PatientRepository patientRepository,
                      PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.bedRepository = bedRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            userRepository.save(User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .role("ROLE_ADMIN")
                    .name("System Admin")
                    .build());

            userRepository.save(User.builder()
                    .username("doctor1")
                    .password(passwordEncoder.encode("doctor123"))
                    .role("ROLE_DOCTOR")
                    .name("Dr. Smith")
                    .build());

            userRepository.save(User.builder()
                    .username("clerk")
                    .password(passwordEncoder.encode("clerk123"))
                    .role("ROLE_OPD_CLERK")
                    .name("OPD Clerk")
                    .build());

            userRepository.save(User.builder()
                    .username("patient1")
                    .password(passwordEncoder.encode("patient123"))
                    .role("ROLE_PATIENT")
                    .name("John Doe")
                    .build());
        }

        if (doctorRepository.count() == 0) {
            Doctor drSmith = Doctor.builder()
                    .name("Dr. Smith")
                    .specialty("Cardiology")
                    .department("Cardiology")
                    .contactNumber("1234567890")
                    .build();

            Doctor drJones = Doctor.builder()
                    .name("Dr. Jones")
                    .specialty("Neurology")
                    .department("Neurology")
                    .contactNumber("0987654321")
                    .build();

            doctorRepository.saveAll(Arrays.asList(drSmith, drJones));

            if (patientRepository.count() == 0) {
                PatientRecord p1 = new PatientRecord();
                p1.setName("John Doe");
                p1.setAge(45);
                p1.setGender("Male");
                p1.setChiefComplaint("Chest pain");
                p1.setAssignedDoctor(drSmith);
                p1.setStatus("ADMITTED");

                PatientRecord p2 = new PatientRecord();
                p2.setName("Jane Doe");
                p2.setAge(30);
                p2.setGender("Female");
                p2.setChiefComplaint("Headache");
                p2.setAssignedDoctor(drJones);
                p2.setStatus("ADMITTED");

                patientRepository.saveAll(Arrays.asList(p1, p2));
            }
        }

        if (bedRepository.count() == 0) {
            // Seed 10 beds: 3 ICU, 3 ER, 4 GENERAL_WARD
            seedBeds("ICU", 3);
            seedBeds("ER", 3);
            seedBeds("GENERAL_WARD", 4);
        }
    }

    private void seedBeds(String type, int count) {
        for (int i = 1; i <= count; i++) {
            bedRepository.save(Bed.builder()
                    .bedNumber("WARD-" + type + "-" + i)
                    .wardName(type + " Ward")
                    .bedType(type)
                    .isOccupied(false)
                    .build());
        }
    }
}
