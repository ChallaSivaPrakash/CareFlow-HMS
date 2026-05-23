package com.careflow.backend.controller;

import com.careflow.backend.entity.PatientRecord;
import com.careflow.backend.service.PatientService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }
    

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_DOCTOR') or hasAuthority('ROLE_OPD_CLERK')")
    public List<PatientRecord> getAllPatients(@RequestParam(required = false) String name, @RequestParam(required = false) String patientId) {
        if (name != null || patientId != null) {
            return patientService.searchPatients(name, patientId);
        }
        return patientService.getAllPatients();
    }
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_DOCTOR') or hasAuthority('ROLE_OPD_CLERK')")
    public ResponseEntity<PatientRecord> getPatientById(@PathVariable Long id) {
        return patientService.getPatientById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/me")
    @PreAuthorize("hasAuthority('ROLE_PATIENT')")
    public ResponseEntity<PatientRecord> getMyRecord() {
        // PROTOTYPE HACK: To get the UI working instantly, we will just return the first patient in the database.
        // In a production app, you would read the JWT Principal here and fetch by patientId!
        return patientService.getAllPatients().stream().findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_OPD_CLERK')")
    public PatientRecord createPatient(@RequestBody PatientRecord patient) {
        return patientService.savePatient(patient);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_DOCTOR') or hasAuthority('ROLE_OPD_CLERK')")
    public ResponseEntity<PatientRecord> updatePatient(@PathVariable Long id, @RequestBody PatientRecord patientDetails) {
        return patientService.getPatientById(id)
                .map(patient -> {
                    patient.setName(patientDetails.getName());
                    patient.setAge(patientDetails.getAge());
                    patient.setGender(patientDetails.getGender());
                    patient.setChiefComplaint(patientDetails.getChiefComplaint());
                    patient.setTriageColor(patientDetails.getTriageColor());
                    patient.setWeight(patientDetails.getWeight());
                    patient.setBloodPressure(patientDetails.getBloodPressure());
                    patient.setHeartRate(patientDetails.getHeartRate());
                    patient.setSpO2(patientDetails.getSpO2());
                    patient.setAssignedDepartment(patientDetails.getAssignedDepartment());
                    patient.setAssignedDoctor(patientDetails.getAssignedDoctor());
                    patient.setStatus(patientDetails.getStatus());
                    return ResponseEntity.ok(patientService.savePatient(patient));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> deletePatient(@PathVariable Long id) {
        return patientService.getPatientById(id)
                .map(patient -> {
                    patientService.deletePatient(id);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
