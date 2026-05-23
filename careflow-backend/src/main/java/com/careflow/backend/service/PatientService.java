package com.careflow.backend.service;

import com.careflow.backend.entity.PatientRecord;
import com.careflow.backend.repository.PatientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PatientService {

    private final PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public List<PatientRecord> getAllPatients() {
        return patientRepository.findAll();
    }

    public Optional<PatientRecord> getPatientById(Long id) {
        return patientRepository.findById(id);
    }

    public PatientRecord savePatient(PatientRecord patient) {
        return patientRepository.save(patient);
    }

    public void deletePatient(Long id) {
        patientRepository.deleteById(id);
    }

    public List<PatientRecord> getPatientsByStatus(String status) {
        return patientRepository.findByStatus(status);
    }

    public List<PatientRecord> getPatientsByDoctorId(Long doctorId) {
        return patientRepository.findByAssignedDoctor_Id(doctorId);
    }

    public List<PatientRecord> searchPatients(String name, String patientId) {
        if (name != null && !name.isEmpty() && patientId != null && !patientId.isEmpty()) {
            return patientRepository.findByNameContainingIgnoreCaseAndPatientIdContainingIgnoreCase(name, patientId);
        } else if (name != null && !name.isEmpty()) {
            return patientRepository.findByNameContainingIgnoreCase(name);
        } else if (patientId != null && !patientId.isEmpty()) {
            return patientRepository.findByPatientIdContainingIgnoreCase(patientId);
        }
        return patientRepository.findAll();
    }
}
