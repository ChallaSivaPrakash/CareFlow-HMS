package com.careflow.backend.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "beds")
public class Bed {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String bedNumber;

    private String wardName;
    private String bedType;
    private boolean isOccupied = false;
    private String currentPatientId;
    private String departmentAssigned;
    private LocalDateTime lastCleanedAt;

    public Bed() {}

    public Bed(String bedNumber, String wardName, String bedType, boolean isOccupied) {
        this.bedNumber = bedNumber;
        this.wardName = wardName;
        this.bedType = bedType;
        this.isOccupied = isOccupied;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getBedNumber() { return bedNumber; }
    public void setBedNumber(String bedNumber) { this.bedNumber = bedNumber; }
    public String getWardName() { return wardName; }
    public void setWardName(String wardName) { this.wardName = wardName; }
    public String getBedType() { return bedType; }
    public void setBedType(String bedType) { this.bedType = bedType; }
    public boolean isOccupied() { return isOccupied; }
    public void setOccupied(boolean occupied) { isOccupied = occupied; }
    public String getCurrentPatientId() { return currentPatientId; }
    public void setCurrentPatientId(String currentPatientId) { this.currentPatientId = currentPatientId; }
    public String getDepartmentAssigned() { return departmentAssigned; }
    public void setDepartmentAssigned(String departmentAssigned) { this.departmentAssigned = departmentAssigned; }
    public LocalDateTime getLastCleanedAt() { return lastCleanedAt; }
    public void setLastCleanedAt(LocalDateTime lastCleanedAt) { this.lastCleanedAt = lastCleanedAt; }

    // Manual Builder
    public static BedBuilder builder() {
        return new BedBuilder();
    }

    public static class BedBuilder {
        private String bedNumber;
        private String wardName;
        private String bedType;
        private boolean isOccupied;

        public BedBuilder bedNumber(String bedNumber) { this.bedNumber = bedNumber; return this; }
        public BedBuilder wardName(String wardName) { this.wardName = wardName; return this; }
        public BedBuilder bedType(String bedType) { this.bedType = bedType; return this; }
        public BedBuilder isOccupied(boolean isOccupied) { this.isOccupied = isOccupied; return this; }

        public Bed build() {
            return new Bed(bedNumber, wardName, bedType, isOccupied);
        }
    }
}
