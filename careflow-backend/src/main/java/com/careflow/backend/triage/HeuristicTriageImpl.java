package com.careflow.backend.triage;

import com.careflow.backend.entity.PatientRecord;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@ConditionalOnProperty(name = "app.triage.mode", havingValue = "heuristic", matchIfMissing = true)
public class HeuristicTriageImpl implements ITriageService {

    @Override
    public PatientRecord processTriage(String chiefComplaint) {
        String complaint = chiefComplaint.toLowerCase();
        PatientRecord record = new PatientRecord();
        
        record.setChiefComplaint(chiefComplaint);
        record.setPatientId("PT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        record.setStatus("WAITING");

        if (complaint.contains("chest") || complaint.contains("heart") || complaint.contains("cardiac")) {
            record.setAssignedDepartment("Cardiology");
            record.setTriageColor("RED");
        } else if (complaint.contains("accident") || complaint.contains("bleeding") || complaint.contains("cut")) {
            record.setAssignedDepartment("Emergency");
            record.setTriageColor("YELLOW");
        } else if (complaint.contains("stroke") || complaint.contains("paralysis")) {
            record.setAssignedDepartment("Neurology");
            record.setTriageColor("RED");
        } else if (complaint.contains("fever") || complaint.contains("cough")) {
            record.setAssignedDepartment("General Medicine");
            record.setTriageColor("GREEN");
        } else {
            record.setAssignedDepartment("General OPD");
            record.setTriageColor("GREEN");
        }

        return record;
    }
}
