package com.careflow.backend.service;

import com.careflow.backend.entity.Bed;
import com.careflow.backend.entity.PatientRecord;
import com.careflow.backend.repository.BedRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BedAllocationService {

    private final BedRepository bedRepository;

    public BedAllocationService(BedRepository bedRepository) {
        this.bedRepository = bedRepository;
    }

    @Transactional
    public Bed assignBed(PatientRecord patient) {
        String bedType;
        String triageColor = patient.getTriageColor();

        if ("RED".equals(triageColor)) {
            bedType = "ICU";
        } else if ("YELLOW".equals(triageColor)) {
            bedType = "ER";
        } else {
            bedType = "GENERAL_WARD";
        }

        List<Bed> availableBeds = bedRepository.findByBedTypeAndIsOccupiedFalse(bedType);
        
        if (!availableBeds.isEmpty()) {
            Bed bed = availableBeds.get(0);
            bed.setOccupied(true);
            bed.setCurrentPatientId(patient.getPatientId());
            return bedRepository.save(bed);
        }

        return null; // Or throw an exception if no beds are available
    }
}
