package com.careflow.hms.security.encryption; 
 
 import org.springframework.beans.factory.annotation.Value; 
 import org.springframework.stereotype.Component; 
 import javax.crypto.Cipher; 
 import javax.crypto.spec.GCMParameterSpec; 
 import javax.crypto.spec.SecretKeySpec; 
 import java.security.SecureRandom; 
 import java.util.Base64; 
 
 @Component 
 public class EncryptionUtil { 
     private static final String ALGORITHM = "AES/GCM/NoPadding"; 
     private static final int GCM_TAG_LENGTH = 128; 
     private static final int IV_LENGTH = 12; 
 
     @Value("${encryption.secret-key}") 
     private String base64SecretKey; 
 
     private SecretKeySpec getKey() { 
         byte[] keyBytes = Base64.getDecoder().decode(base64SecretKey); 
         return new SecretKeySpec(keyBytes, "AES"); 
     } 
 
     public String encrypt(String plaintext) { 
         if (plaintext == null) return null; 
         try { 
             Cipher cipher = Cipher.getInstance(ALGORITHM); 
             byte[] iv = new byte[IV_LENGTH]; 
             SecureRandom.getInstanceStrong().nextBytes(iv); 
             GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv); 
             cipher.init(Cipher.ENCRYPT_MODE, getKey(), spec); 
             byte[] ciphertext = cipher.doFinal(plaintext.getBytes()); 
             byte[] encrypted = new byte[IV_LENGTH + ciphertext.length]; 
             System.arraycopy(iv, 0, encrypted, 0, IV_LENGTH); 
             System.arraycopy(ciphertext, 0, encrypted, IV_LENGTH, ciphertext.length); 
             return Base64.getEncoder().encodeToString(encrypted); 
         } catch (Exception e) { 
             throw new RuntimeException("Encryption failed", e); 
         } 
     } 
 
     public String decrypt(String encryptedData) { 
         if (encryptedData == null) return null; 
         try { 
             byte[] decoded = Base64.getDecoder().decode(encryptedData); 
             Cipher cipher = Cipher.getInstance(ALGORITHM); 
             GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, decoded, 0, IV_LENGTH); 
             cipher.init(Cipher.DECRYPT_MODE, getKey(), spec); 
             return new String(cipher.doFinal(decoded, IV_LENGTH, decoded.length - IV_LENGTH)); 
         } catch (Exception e) { 
             throw new RuntimeException("Decryption failed", e); 
         } 
     } 
 } 
