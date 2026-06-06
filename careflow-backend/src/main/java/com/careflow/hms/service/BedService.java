package com.careflow.hms.service; 
 
 import com.careflow.hms.entity.Bed; 
 import com.careflow.hms.repository.BedRepository; 
 import org.springframework.stereotype.Service; 
 
 import java.util.List; 
 
 @Service 
 public class BedService { 
 
     private final BedRepository bedRepository; 
 
     public BedService(BedRepository bedRepository) { 
         this.bedRepository = bedRepository; 
     } 
 
     public List<Bed> getOccupancyGrid() { 
         return bedRepository.findAll(); 
     } 
 
     public Bed assignPatientToBed(Long bedId, Long patientId) { 
         Bed bed = bedRepository.findById(bedId) 
                 .orElseThrow(() -> new RuntimeException("Bed not found")); 
         bed.setPatientId(patientId); 
         bed.setOccupied(true); 
         return bedRepository.save(bed); 
     } 
 
     public Bed releaseBed(Long bedId) { 
         Bed bed = bedRepository.findById(bedId) 
                 .orElseThrow(() -> new RuntimeException("Bed not found")); 
         bed.setPatientId(null); 
         bed.setOccupied(false); 
         return bedRepository.save(bed); 
     } 
 } 
