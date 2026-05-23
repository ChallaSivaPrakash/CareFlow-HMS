package com.careflow.backend.triage;

import com.careflow.backend.entity.PatientRecord;

public interface ITriageService {
    PatientRecord processTriage(String chiefComplaint);
}
