package com.careflow.hms.controller; 
 
 import com.careflow.hms.entity.Bed; 
 import com.careflow.hms.model.RecoveryIndex; 
 import com.careflow.hms.service.BedService; 
 import com.careflow.hms.service.ClinicalService; 
 import org.springframework.http.ResponseEntity; 
 import org.springframework.web.bind.annotation.*; 
 
 import java.util.List; 
 
 @RestController 
 @RequestMapping("/api") 
 @CrossOrigin(origins = "http://localhost:4200") 
 public class BedController { 
 
     private final BedService bedService; 
     private final ClinicalService clinicalService; 
 
     public BedController(BedService bedService, ClinicalService clinicalService) { 
         this.bedService = bedService; 
         this.clinicalService = clinicalService; 
     } 
 
     @GetMapping("/beds/occupancy") 
     public ResponseEntity<List<Bed>> getBedOccupancy() { 
         return ResponseEntity.ok(bedService.getOccupancyGrid()); 
     } 
 
     @PostMapping("/beds/{bedId}/assign/{patientId}") 
     public ResponseEntity<Bed> assignBed(@PathVariable Long bedId, @PathVariable Long patientId) { 
         return ResponseEntity.ok(bedService.assignPatientToBed(bedId, patientId)); 
     } 
 
     @PostMapping("/beds/{bedId}/release") 
     public ResponseEntity<Bed> releaseBed(@PathVariable Long bedId) { 
         return ResponseEntity.ok(bedService.releaseBed(bedId)); 
     } 
 
     @GetMapping("/patients/{id}/recovery-index") 
     public ResponseEntity<RecoveryIndex> getRecoveryIndex(@PathVariable Long id) { 
         return ResponseEntity.ok(clinicalService.calculateRecoveryIndex(id)); 
     } 
 } 
