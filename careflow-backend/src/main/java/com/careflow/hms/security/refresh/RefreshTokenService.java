package com.careflow.hms.security.refresh; 
 
 import org.springframework.beans.factory.annotation.Value; 
 import org.springframework.stereotype.Service; 
 import org.springframework.transaction.annotation.Transactional; 
 import java.time.Instant; 
 import java.util.Optional;
 import java.util.UUID; 
 
 @Service 
 public class RefreshTokenService { 
     @Value("${app.jwt.refresh.expiration-ms:604800000}") 
     private long refreshExpirationMs; 
 
     private final RefreshTokenRepository refreshTokenRepository; 
 
     public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) { 
         this.refreshTokenRepository = refreshTokenRepository; 
     } 
 
     public Optional<RefreshToken> findByToken(String token) {
         return refreshTokenRepository.findByToken(token);
     }

     @Transactional
     public RefreshToken createRefreshToken(String username) { 
         refreshTokenRepository.deleteByUsername(username); 
         RefreshToken refreshToken = new RefreshToken( 
             UUID.randomUUID().toString(), 
             username, 
             Instant.now().plusMillis(refreshExpirationMs) 
         ); 
         return refreshTokenRepository.save(refreshToken); 
     } 
 
     public RefreshToken verifyExpiration(RefreshToken token) { 
         if (token.getExpiryDate().compareTo(Instant.now()) < 0) { 
             refreshTokenRepository.delete(token); 
             throw new RuntimeException("Refresh token expired. Please login again."); 
         } 
         return token; 
     } 
 
     @Transactional 
     public void revokeAllUserTokens(String username) { 
         refreshTokenRepository.deleteByUsername(username); 
     } 
 } 
