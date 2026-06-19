package com.careflow.hms.config;

import com.careflow.hms.entity.User;
import com.careflow.hms.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataSeeder.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (!userRepository.existsByUsername("admin")) {
            User adminUser = new User();
            adminUser.setUsername("admin");
            adminUser.setPassword(passwordEncoder.encode("admin123"));
            adminUser.setRole("ROLE_ADMIN");
            adminUser.setName("System Admin");
            adminUser.setActive(true);

            userRepository.save(adminUser);
            logger.info("Default admin user created: admin / admin123");
        } else {
            logger.info("Admin user already exists, skipping seed.");
        }

        if (!userRepository.existsByUsername("dr.sravan")) {
            User doctorUser = new User();
            doctorUser.setUsername("dr.sravan");
            doctorUser.setPassword(passwordEncoder.encode("Doctor@2026"));
            doctorUser.setRole("ROLE_DOCTOR");
            doctorUser.setName("Dr. Sravan");
            doctorUser.setActive(true);

            userRepository.save(doctorUser);
            logger.info("Default doctor user created: dr.sravan / Doctor@2026");
        } else {
            logger.info("Doctor user already exists, skipping seed.");
        }

        if (!userRepository.existsByUsername("clerk")) {
            User clerkUser = new User();
            clerkUser.setUsername("clerk");
            clerkUser.setPassword(passwordEncoder.encode("clerk123"));
            clerkUser.setRole("ROLE_OPD_CLERK");
            clerkUser.setName("Front Desk Clerk");
            clerkUser.setActive(true);

            userRepository.save(clerkUser);
            logger.info("Default clerk user created: clerk / clerk123");
        } else {
            userRepository.findByUsername("clerk").ifPresent(existingClerk -> {
                existingClerk.setPassword(passwordEncoder.encode("Clerk@1206"));
                existingClerk.setRole("ROLE_OPD_CLERK");
                userRepository.save(existingClerk);
                logger.info("Clerk user password and role reset: clerk / Clerk@1206");
            });
        }
    }
}
