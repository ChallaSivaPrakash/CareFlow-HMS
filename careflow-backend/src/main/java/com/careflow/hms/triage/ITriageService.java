package com.careflow.hms.triage;

import com.careflow.hms.entity.Patient;

public interface ITriageService {
    Patient processTriage(Patient patient);
}
