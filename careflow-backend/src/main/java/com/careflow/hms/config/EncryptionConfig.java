package com.careflow.hms.config; 
 
 import com.careflow.hms.security.encryption.EncryptedStringConverter; 
 import com.careflow.hms.security.encryption.EncryptionUtil; 
 import jakarta.annotation.PostConstruct; 
 import org.springframework.context.annotation.Configuration; 
 
 @Configuration 
 public class EncryptionConfig { 
 
     private final EncryptionUtil encryptionUtil; 
 
     public EncryptionConfig(EncryptionUtil encryptionUtil) { 
         this.encryptionUtil = encryptionUtil; 
     } 
 
     @PostConstruct 
     public void initEncryptionConverter() { 
         EncryptedStringConverter.init(encryptionUtil); 
     } 
 } 
