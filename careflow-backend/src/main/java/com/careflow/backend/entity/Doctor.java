package com.careflow.backend.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "doctors")
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String specialty;
    private String department;
    private String contactNumber;
    private String email;
    private String address;

    @Column(columnDefinition = "TEXT")
    private String bio;
    
    // NEW: Profile Image URL
    @Column(columnDefinition = "LONGTEXT")
    private String profileImageUrl;

    private boolean isActive = true;

    @OneToMany(mappedBy = "assignedDoctor", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<PatientRecord> patients = new ArrayList<>();

    public Doctor() {}

    public Doctor(String name, String specialty, String department, String contactNumber, String profileImageUrl) {
        this.name = name;
        this.specialty = specialty;
        this.department = department;
        this.contactNumber = contactNumber;
        this.profileImageUrl = profileImageUrl;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSpecialty() { return specialty; }
    public void setSpecialty(String specialty) { this.specialty = specialty; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    public String getProfileImageUrl() { return profileImageUrl; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
    public List<PatientRecord> getPatients() { return patients; }
    public void setPatients(List<PatientRecord> patients) { this.patients = patients; }

    // Manual Builder
    public static DoctorBuilder builder() {
        return new DoctorBuilder();
    }

    public static class DoctorBuilder {
        private String name;
        private String specialty;
        private String department;
        private String contactNumber;
        private String email;
        private String address;
        private String bio;
        private String profileImageUrl;

        public DoctorBuilder name(String name) { this.name = name; return this; }
        public DoctorBuilder specialty(String specialty) { this.specialty = specialty; return this; }
        public DoctorBuilder department(String department) { this.department = department; return this; }
        public DoctorBuilder contactNumber(String contactNumber) { this.contactNumber = contactNumber; return this; }
        public DoctorBuilder email(String email) { this.email = email; return this; }
        public DoctorBuilder address(String address) { this.address = address; return this; }
        public DoctorBuilder bio(String bio) { this.bio = bio; return this; }
        public DoctorBuilder profileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; return this; }

        public Doctor build() {
            Doctor doctor = new Doctor(name, specialty, department, contactNumber, profileImageUrl);
            doctor.setEmail(email);
            doctor.setAddress(address);
            doctor.setBio(bio);
            return doctor;
        }
    }
}