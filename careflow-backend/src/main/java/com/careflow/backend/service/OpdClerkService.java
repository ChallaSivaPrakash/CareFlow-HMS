package com.careflow.backend.service;

import com.careflow.backend.entity.OpdClerk;
import com.careflow.backend.entity.User;
import com.careflow.backend.repository.OpdClerkRepository;
import com.careflow.backend.repository.UserRepository;
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

            User newClerkLogin = User.builder()
                    .username(generatedUsername)
                    .password(passwordEncoder.encode(defaultPassword))
                    .role("ROLE_OPD_CLERK")
                    .name(clerk.getName())
                    .build();
            
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
