package com.careflow.hms.entity;

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

    private String bedType; // ICU, ER, GENERAL_WARD, OBSERVATION, MATERNITY, BURNS

    private Boolean isOccupied = false;

    private String currentPatientId;

    private String departmentAssigned;

    private LocalDateTime lastCleanedAt;

    public Bed() {
    }

    public Bed(Long id, String bedNumber, String wardName, String bedType, Boolean isOccupied, String currentPatientId, String departmentAssigned, LocalDateTime lastCleanedAt) {
        this.id = id;
        this.bedNumber = bedNumber;
        this.wardName = wardName;
        this.bedType = bedType;
        this.isOccupied = isOccupied;
        this.currentPatientId = currentPatientId;
        this.departmentAssigned = departmentAssigned;
        this.lastCleanedAt = lastCleanedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBedNumber() {
        return bedNumber;
    }

    public void setBedNumber(String bedNumber) {
        this.bedNumber = bedNumber;
    }

    public String getWardName() {
        return wardName;
    }

    public void setWardName(String wardName) {
        this.wardName = wardName;
    }

    public String getBedType() {
        return bedType;
    }

    public void setBedType(String bedType) {
        this.bedType = bedType;
    }

    public Boolean getIsOccupied() {
        return isOccupied;
    }

    public void setIsOccupied(Boolean isOccupied) {
        this.isOccupied = isOccupied;
    }

    public String getCurrentPatientId() {
        return currentPatientId;
    }

    public void setCurrentPatientId(String currentPatientId) {
        this.currentPatientId = currentPatientId;
    }

    public String getDepartmentAssigned() {
        return departmentAssigned;
    }

    public void setDepartmentAssigned(String departmentAssigned) {
        this.departmentAssigned = departmentAssigned;
    }

    public LocalDateTime getLastCleanedAt() {
        return lastCleanedAt;
    }

    public void setLastCleanedAt(LocalDateTime lastCleanedAt) {
        this.lastCleanedAt = lastCleanedAt;
    }

    @Override
    public String toString() {
        return "Bed{" +
                "id=" + id +
                ", bedNumber='" + bedNumber + '\'' +
                ", bedType='" + bedType + '\'' +
                ", isOccupied=" + isOccupied +
                '}';
    }
}
