package com.careflow.hms.triage;

import com.careflow.hms.entity.Patient;

public interface ITriageService {
    Patient processTriage(Patient patient);
    
    default TriageResult suggestTriage(PatientIntake intake) {
        return new TriageResult("GREEN", "Defaulting to GREEN", true);
    }
}
