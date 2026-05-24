package com.careflow.hms.controller;

import com.careflow.hms.dto.AuthResponse;
import com.careflow.hms.dto.LoginRequest;
import com.careflow.hms.entity.User;
import com.careflow.hms.repository.UserRepository;
import com.careflow.hms.security.JwtTokenProvider;
import com.careflow.hms.service.PasswordResetService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class AuthController {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetService passwordResetService;

    public AuthController(UserRepository userRepository, AuthenticationManager authenticationManager, JwtTokenProvider tokenProvider, PasswordEncoder passwordEncoder, PasswordResetService passwordResetService) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.passwordEncoder = passwordEncoder;
        this.passwordResetService = passwordResetService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            return ResponseEntity.badRequest().body("Username is already taken!");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        String jwt = tokenProvider.generateToken(authentication);
        String role = tokenProvider.extractRole(jwt);

        return ResponseEntity.ok(new AuthResponse(jwt, role, loginRequest.getUsername()));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        // For simplicity assuming username is email or email is stored in name/other field
        // instruction said check if email exists in UserRepository.
        // I'll check if username exists since I don't have an email field in User.
        if (userRepository.findByUsername(email).isPresent()) {
            passwordResetService.sendOtp(email);
            return ResponseEntity.ok("OTP sent to your email");
        }
        return ResponseEntity.badRequest().body("User not found");
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String otp = request.get("otp");
        if (passwordResetService.verifyOtp(email, otp)) {
            return ResponseEntity.ok(Map.of("successToken", "VALID_OTP_TOKEN_" + email));
        }
        return ResponseEntity.badRequest().body("Invalid or expired OTP");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String successToken = request.get("successToken");
        String newPassword = request.get("password");

        if (successToken.equals("VALID_OTP_TOKEN_" + email)) {
            return userRepository.findByUsername(email)
                    .map(user -> {
                        user.setPassword(passwordEncoder.encode(newPassword));
                        userRepository.save(user);
                        passwordResetService.deleteOtp(email);
                        return ResponseEntity.ok("Password reset successful");
                    })
                    .orElse(ResponseEntity.badRequest().body("User not found"));
        }
        return ResponseEntity.badRequest().body("Invalid token");
    }
}
