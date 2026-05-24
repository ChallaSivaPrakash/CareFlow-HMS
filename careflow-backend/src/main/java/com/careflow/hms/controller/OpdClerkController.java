package com.careflow.hms.controller;

import com.careflow.hms.entity.OpdClerk;
import com.careflow.hms.service.OpdClerkService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clerks")
public class OpdClerkController {

    private final OpdClerkService opdClerkService;

    public OpdClerkController(OpdClerkService opdClerkService) {
        this.opdClerkService = opdClerkService;
    }

    @GetMapping
    public List<OpdClerk> getAllClerks() {
        return opdClerkService.getAllActiveClerks();
    }

    @GetMapping("/{id}")
    public ResponseEntity<OpdClerk> getClerkById(@PathVariable Long id) {
        return opdClerkService.getClerkById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public OpdClerk createClerk(@RequestBody OpdClerk clerk) {
        return opdClerkService.saveClerk(clerk);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OpdClerk> updateClerk(@PathVariable Long id, @RequestBody OpdClerk clerkDetails) {
        return opdClerkService.getClerkById(id)
                .map(clerk -> {
                    clerk.setName(clerkDetails.getName());
                    clerk.setEmail(clerkDetails.getEmail());
                    clerk.setContactNumber(clerkDetails.getContactNumber());
                    clerk.setDepartment(clerkDetails.getDepartment());
                    clerk.setAddress(clerkDetails.getAddress());
                    clerk.setBio(clerkDetails.getBio());
                    clerk.setProfileImageUrl(clerkDetails.getProfileImageUrl());
                    return ResponseEntity.ok(opdClerkService.saveClerk(clerk));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteClerk(@PathVariable Long id) {
        opdClerkService.deactivateClerk(id);
        return ResponseEntity.ok().build();
    }
}
