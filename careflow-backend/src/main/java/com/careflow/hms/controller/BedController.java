package com.careflow.hms.controller;

import com.careflow.hms.entity.Bed;
import com.careflow.hms.repository.BedRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/beds")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class BedController {

    private final BedRepository bedRepository;

    public BedController(BedRepository bedRepository) {
        this.bedRepository = bedRepository;
    }

    @GetMapping("/available")
    public ResponseEntity<List<Bed>> getAvailableBeds() {
        return ResponseEntity.ok(bedRepository.findByIsOccupiedFalse());
    }

    @GetMapping("/suggest")
    public ResponseEntity<Bed> suggestBed(@RequestParam String triageColor) {
        String bedType = "GENERAL_WARD";
        if ("RED".equalsIgnoreCase(triageColor)) bedType = "ICU";
        else if ("YELLOW".equalsIgnoreCase(triageColor)) bedType = "ER";

        List<Bed> available = bedRepository.findByBedTypeAndIsOccupiedFalse(bedType);
        if (available.isEmpty()) {
            // Fallback to any available bed if preferred type is full
            available = bedRepository.findByIsOccupiedFalse();
        }

        return available.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(available.get(0));
    }

    @PostMapping("/allocate")
    public ResponseEntity<Bed> allocateBed(@RequestParam Long bedId, @RequestParam String patientId) {
        return bedRepository.findById(bedId)
                .map(bed -> {
                    bed.setIsOccupied(true);
                    bed.setCurrentPatientId(patientId);
                    return ResponseEntity.ok(bedRepository.save(bed));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/occupy")
    public ResponseEntity<?> occupyBed(@PathVariable Long id, @RequestParam String patientId) {
        return bedRepository.findById(id)
                .map(bed -> {
                    bed.setIsOccupied(true);
                    bed.setCurrentPatientId(patientId);
                    bedRepository.save(bed);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
