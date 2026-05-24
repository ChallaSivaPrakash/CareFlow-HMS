package com.careflow.hms.service;

import com.careflow.hms.entity.OpdClerk;
import com.careflow.hms.entity.User;
import com.careflow.hms.repository.OpdClerkRepository;
import com.careflow.hms.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class OpdClerkService {

    private final OpdClerkRepository opdClerkRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public OpdClerkService(OpdClerkRepository opdClerkRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.opdClerkRepository = opdClerkRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<OpdClerk> getAllActiveClerks() {
        return opdClerkRepository.findByIsActiveTrue();
    }

    public Optional<OpdClerk> getClerkById(Long id) {
        return opdClerkRepository.findById(id);
    }

    public OpdClerk saveClerk(OpdClerk clerk) {
        if (clerk.getId() == null) {
            String generatedUsername = clerk.getName().replaceAll("\\s+", "").toLowerCase();
            String defaultPassword = "Clerk@2026";

            // Assuming your User class has a constructor that accepts these fields
        User newClerkLogin = new User();
newClerkLogin.setUsername(generatedUsername);
newClerkLogin.setPassword(passwordEncoder.encode(defaultPassword));
newClerkLogin.setRole("ROLE_OPD_CLERK");
newClerkLogin.setName(clerk.getName());
newClerkLogin.setCreatedAt(java.time.LocalDateTime.now());
newClerkLogin.setUpdatedAt(java.time.LocalDateTime.now());
            
            userRepository.save(newClerkLogin);
        }
        return opdClerkRepository.save(clerk);
    }

    public void deactivateClerk(Long id) {
        opdClerkRepository.findById(id).ifPresent(clerk -> {
            clerk.setActive(false);
            opdClerkRepository.save(clerk);

            String generatedUsername = clerk.getName().replaceAll("\\s+", "").toLowerCase();
            userRepository.findByUsername(generatedUsername).ifPresent(user -> {
                user.setActive(false);
                userRepository.save(user);
            });
        });
    }
}
