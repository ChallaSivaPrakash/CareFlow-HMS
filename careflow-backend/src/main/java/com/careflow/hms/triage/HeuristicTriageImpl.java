package com.careflow.hms.triage;

import com.careflow.hms.entity.Patient;
import com.careflow.hms.service.AuditService;
import org.springframework.stereotype.Service;

@Service
public class HeuristicTriageImpl implements ITriageService {

    private final AuditService auditService;

    public HeuristicTriageImpl(AuditService auditService) {
        this.auditService = auditService;
    }

    @Override
    public Patient processTriage(Patient patient) {
        String complaint = patient.getChiefComplaint().toLowerCase();

        if (complaint.contains("chest pain") || complaint.contains("heart attack")) {
            patient.setTriageColor("RED");
            patient.setAssignedDepartment("Cardiology");
        } else if (complaint.contains("accident") || complaint.contains("bleeding")) {
            patient.setTriageColor("YELLOW");
            patient.setAssignedDepartment("Emergency");
        } else if (complaint.contains("stroke") || complaint.contains("paralysis")) {
            patient.setTriageColor("RED");
            patient.setAssignedDepartment("Neurology");
        } else if (patient.getTriageColor() != null && patient.getTriageColor().equals("GREEN")) {
            patient.setTriageColor("GREEN");
            patient.setAssignedDepartment("General OPD / General Ward");
        } else {
            patient.setTriageColor("GREEN");
            patient.setAssignedDepartment("General OPD");
        }

        patient.setStatus("WAITING");

        auditService.logAction(
                "SYSTEM",
                "Heuristic Triage Engine",
                "PROCESS_TRIAGE",
                "Patient",
                patient.getPatientId(),
                "Triage processed for patient: " + patient.getName() + ". Severity: " + patient.getTriageColor()
        );

        return patient;
    }
}
