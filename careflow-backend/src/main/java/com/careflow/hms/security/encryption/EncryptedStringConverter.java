package com.careflow.hms.security.encryption; 
 
 import jakarta.persistence.AttributeConverter; 
 import jakarta.persistence.Converter; 
 
 @Converter 
 public class EncryptedStringConverter implements AttributeConverter<String, String> { 
 
     private static EncryptionUtil encryptionUtil; 
 
     // Lazy init to avoid circular dependency with Spring Boot 
     public static void init(EncryptionUtil util) { 
         encryptionUtil = util; 
     } 
 
     @Override 
     public String convertToDatabaseColumn(String attribute) { 
         if (encryptionUtil == null) return attribute;
         return encryptionUtil.encrypt(attribute); 
     } 
 
     @Override 
     public String convertToEntityAttribute(String dbData) { 
         if (encryptionUtil == null) return dbData;
         return encryptionUtil.decrypt(dbData); 
     } 
 } 
