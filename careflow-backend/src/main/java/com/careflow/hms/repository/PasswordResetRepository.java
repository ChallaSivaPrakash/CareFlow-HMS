package com.careflow.hms.repository;

import com.careflow.hms.entity.PasswordReset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PasswordResetRepository extends JpaRepository<PasswordReset, Long> {
    Optional<PasswordReset> findByEmail(String email);
    Optional<PasswordReset> findByEmailAndOtp(String email, String otp);
    void deleteByEmail(String email);
}
