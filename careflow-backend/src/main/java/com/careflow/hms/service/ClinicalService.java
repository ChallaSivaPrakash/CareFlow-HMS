package com.careflow.hms.service; 
 
 import com.careflow.hms.entity.Patient; 
 import com.careflow.hms.model.RecoveryIndex; 
 import com.careflow.hms.repository.PatientRepository; 
 import org.springframework.stereotype.Service; 
 
 @Service 
 public class ClinicalService { 
 
     private final PatientRepository patientRepository; 
 
     public ClinicalService(PatientRepository patientRepository) { 
         this.patientRepository = patientRepository; 
     } 
 
     public RecoveryIndex calculateRecoveryIndex(Long patientId) { 
         Patient patient = patientRepository.findById(patientId) 
                 .orElseThrow(() -> new RuntimeException("Patient not found")); 
         
         RecoveryIndex index = new RecoveryIndex(); 
         index.setPatientId(patientId); 
         index.setPatientName(patient.getName()); 
         
         int score = 50; 
         
         if (patient.getSpO2() != null) { 
             if (patient.getSpO2() >= 95) score += 15; 
             else if (patient.getSpO2() >= 90) score += 5; 
             else score -= 10; 
         } 
         
         if (patient.getHeartRate() != null) { 
             if (patient.getHeartRate() >= 60 && patient.getHeartRate() <= 100) score += 15; 
             else score -= 10; 
         } 
         
         if ("DISCHARGED".equals(patient.getStatus())) score = 100; 
         else if ("CRITICAL".equals(patient.getStatus())) score = Math.min(score, 30); 
         
         index.setScore(Math.max(0, Math.min(100, score))); 
         
         if (index.getScore() >= 80) index.setTrend("IMPROVING"); 
         else if (index.getScore() >= 40) index.setTrend("STABLE"); 
         else index.setTrend("CRITICAL"); 
         
         return index; 
     } 
 } 
