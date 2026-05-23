package com.careflow.hms.service;

import com.careflow.hms.entity.PasswordReset;
import com.careflow.hms.repository.PasswordResetRepository;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Random;

@Service
public class PasswordResetService {

    private final PasswordResetRepository passwordResetRepository;
    private final JavaMailSender mailSender;

    public PasswordResetService(PasswordResetRepository passwordResetRepository, JavaMailSender mailSender) {
        this.passwordResetRepository = passwordResetRepository;
        this.mailSender = mailSender;
    }

    public void sendOtp(String email) {
        String otp = String.format("%06d", new Random().nextInt(999999));
        PasswordReset reset = passwordResetRepository.findByEmail(email)
                .orElse(new PasswordReset());
        
        reset.setEmail(email);
        reset.setOtp(otp);
        reset.setExpiryTimestamp(LocalDateTime.now().plusMinutes(10));
        passwordResetRepository.save(reset);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("CareFlow HMS - Password Reset OTP");
        message.setText("Your OTP for password reset is: " + otp + ". It expires in 10 minutes.");
        
        try {
            mailSender.send(message);
        } catch (Exception e) {
            System.out.println("Mock Email sent to " + email + " with OTP: " + otp);
        }
    }

    public boolean verifyOtp(String email, String otp) {
        return passwordResetRepository.findByEmailAndOtp(email, otp)
                .map(reset -> reset.getExpiryTimestamp().isAfter(LocalDateTime.now()))
                .orElse(false);
    }

    public void deleteOtp(String email) {
        passwordResetRepository.deleteByEmail(email);
    }
}
