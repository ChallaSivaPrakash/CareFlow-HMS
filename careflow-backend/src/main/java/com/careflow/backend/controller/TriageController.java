package com.careflow.backend.controller;

import com.careflow.backend.entity.Bed;
import com.careflow.backend.entity.PatientRecord;
import com.careflow.backend.repository.PatientRepository;
import com.careflow.backend.service.BedAllocationService;
import com.careflow.backend.service.WebSocketNotificationService;
import com.careflow.backend.triage.ITriageService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/triage")
public class TriageController {

    private final ITriageService triageService;
    private final BedAllocationService bedAllocationService;
    private final WebSocketNotificationService notificationService;
    private final PatientRepository patientRepository;

    public TriageController(ITriageService triageService,
                            BedAllocationService bedAllocationService,
                            WebSocketNotificationService notificationService,
                            PatientRepository patientRepository) {
        this.triageService = triageService;
        this.bedAllocationService = bedAllocationService;
        this.notificationService = notificationService;
        this.patientRepository = patientRepository;
    }

    @PostMapping
    public PatientRecord performTriage(@RequestBody Map<String, String> request) {
        String chiefComplaint = request.get("chiefComplaint");
        
        // 1. Process Triage
        PatientRecord patient = triageService.processTriage(chiefComplaint);
        
        // 2. Assign Bed
        Bed assignedBed = bedAllocationService.assignBed(patient);
        if (assignedBed != null) {
            patient.setAssignedBedId(assignedBed.getBedNumber());
        }
        
        // 3. Save Patient
        PatientRecord savedPatient = patientRepository.save(patient);
        
        // 4. Send Alert if emergency (RED or YELLOW)
        if ("RED".equals(savedPatient.getTriageColor()) || "YELLOW".equals(savedPatient.getTriageColor())) {
            notificationService.sendEmergencyAlert(savedPatient);
        }
        
        return savedPatient;
    }
}
