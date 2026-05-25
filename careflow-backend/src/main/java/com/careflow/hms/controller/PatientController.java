package com.careflow.hms.controller;

import com.careflow.hms.entity.Doctor;
import com.careflow.hms.entity.Patient;
import com.careflow.hms.repository.DoctorRepository;
import com.careflow.hms.repository.PatientRepository;
import com.careflow.hms.service.AuditService;
import com.careflow.hms.service.WebSocketNotificationService;
import com.careflow.hms.triage.ITriageService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/patients")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class PatientController {

    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final ITriageService triageService;
    private final WebSocketNotificationService webSocketNotificationService;
    private final AuditService auditService;

    public PatientController(PatientRepository patientRepository, DoctorRepository doctorRepository, ITriageService triageService, WebSocketNotificationService webSocketNotificationService, AuditService auditService) {
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.triageService = triageService;
        this.webSocketNotificationService = webSocketNotificationService;
        this.auditService = auditService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OPD_CLERK')")
    public ResponseEntity<Patient> createPatient(@RequestBody Patient patient) {
        Patient triagedPatient = triageService.processTriage(patient);
        Patient savedPatient = patientRepository.save(triagedPatient);
        
        auditService.logAction(getCurrentUser(), getCurrentUser(), "CREATE_PATIENT", "Patient", savedPatient.getPatientId(), "Patient created with visitType: " + savedPatient.getVisitType());
        
        webSocketNotificationService.sendEmergencyAlert(savedPatient);
        return ResponseEntity.ok(savedPatient);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OPD_CLERK', 'DOCTOR')")
    public ResponseEntity<List<Patient>> getAllPatients() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_DOCTOR"))) {
            String currentUsername = auth.getName();
            return doctorRepository.findByEmail(currentUsername)
                    .map(doctor -> ResponseEntity.ok(patientRepository.findByAssignedDoctorId(doctor.getId())))
                    .orElse(ResponseEntity.ok(List.of())); // Return empty if doctor record not found
        }

        return ResponseEntity.ok(patientRepository.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPD_CLERK', 'DOCTOR')")
    public ResponseEntity<Patient> getPatientById(@PathVariable Long id) {
        return patientRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPD_CLERK', 'DOCTOR')")
    public ResponseEntity<Patient> updatePatient(@PathVariable Long id, @RequestBody Patient patientDetails) {
        return patientRepository.findById(id)
                .map(patient -> {
                    String oldStatus = patient.getStatus();
                    patient.setName(patientDetails.getName());
                    patient.setAge(patientDetails.getAge());
                    patient.setGender(patientDetails.getGender());
                    patient.setChiefComplaint(patientDetails.getChiefComplaint());
                    patient.setStatus(patientDetails.getStatus());
                    patient.setTriageColor(patientDetails.getTriageColor());
                    patient.setAssignedDoctor(patientDetails.getAssignedDoctor());
                    patient.setAssignedDepartment(patientDetails.getAssignedDepartment());
                    Patient updatedPatient = patientRepository.save(patient);
                    
                    if ("DISCHARGED".equals(updatedPatient.getStatus()) && !"DISCHARGED".equals(oldStatus)) {
                        auditService.logAction(getCurrentUser(), getCurrentUser(), "DISCHARGE_PATIENT", "Patient", updatedPatient.getPatientId(), "Patient discharged");
                    } else {
                        auditService.logAction(getCurrentUser(), getCurrentUser(), "UPDATE_PATIENT", "Patient", updatedPatient.getPatientId(), "Patient details updated");
                    }
                    
                    return ResponseEntity.ok(updatedPatient);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/assign")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<Patient> assignDoctor(@PathVariable Long id, @RequestBody Doctor doctor) {
        return patientRepository.findById(id)
                .map(patient -> {
                    patient.setAssignedDoctor(doctor);
                    patient.setStatus("IN_CONSULTATION");
                    Patient updated = patientRepository.save(patient);
                    auditService.logAction(getCurrentUser(), getCurrentUser(), "ASSIGN_DOCTOR", "Patient", updated.getPatientId(), "Doctor " + doctor.getName() + " claimed the patient");
                    return ResponseEntity.ok(updated);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    private String getCurrentUser() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @GetMapping("/doctor/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<List<Patient>> getPatientsByDoctor(@PathVariable Long id) {
        return ResponseEntity.ok(patientRepository.findByAssignedDoctorId(id));
    }

    @DeleteMapping("/{id}")
    @Deprecated
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePatient(@PathVariable Long id) {
        // Deprecated: Use status update to 'DISCHARGED' instead
        return ResponseEntity.status(410).build();
    }
}
