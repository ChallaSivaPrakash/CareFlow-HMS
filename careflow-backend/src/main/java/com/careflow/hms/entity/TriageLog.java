package com.careflow.hms.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Table(name = "triage_logs")
public class TriageLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String tempPatientId;

    private String arrivalTime;

    @Size(max = 1000)
    private String voiceText;

    private String extractedDepartment;

    private String extractedSeverity; // STABLE, MODERATE, HIGH, CRITICAL

    private String suggestedBedType;

    private String assignedDoctorId;

    private String assignedBedId;

    private String status; // PENDING, ASSIGNED, STABILIZED

    private LocalDateTime createdAt;

    public TriageLog() {
    }

    public TriageLog(Long id, String tempPatientId, String arrivalTime, String voiceText, String extractedDepartment, String extractedSeverity, String suggestedBedType, String assignedDoctorId, String assignedBedId, String status, LocalDateTime createdAt) {
        this.id = id;
        this.tempPatientId = tempPatientId;
        this.arrivalTime = arrivalTime;
        this.voiceText = voiceText;
        this.extractedDepartment = extractedDepartment;
        this.extractedSeverity = extractedSeverity;
        this.suggestedBedType = suggestedBedType;
        this.assignedDoctorId = assignedDoctorId;
        this.assignedBedId = assignedBedId;
        this.status = status;
        this.createdAt = createdAt;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTempPatientId() {
        return tempPatientId;
    }

    public void setTempPatientId(String tempPatientId) {
        this.tempPatientId = tempPatientId;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public String getVoiceText() {
        return voiceText;
    }

    public void setVoiceText(String voiceText) {
        this.voiceText = voiceText;
    }

    public String getExtractedDepartment() {
        return extractedDepartment;
    }

    public void setExtractedDepartment(String extractedDepartment) {
        this.extractedDepartment = extractedDepartment;
    }

    public String getExtractedSeverity() {
        return extractedSeverity;
    }

    public void setExtractedSeverity(String extractedSeverity) {
        this.extractedSeverity = extractedSeverity;
    }

    public String getSuggestedBedType() {
        return suggestedBedType;
    }

    public void setSuggestedBedType(String suggestedBedType) {
        this.suggestedBedType = suggestedBedType;
    }

    public String getAssignedDoctorId() {
        return assignedDoctorId;
    }

    public void setAssignedDoctorId(String assignedDoctorId) {
        this.assignedDoctorId = assignedDoctorId;
    }

    public String getAssignedBedId() {
        return assignedBedId;
    }

    public void setAssignedBedId(String assignedBedId) {
        this.assignedBedId = assignedBedId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "TriageLog{" +
                "id=" + id +
                ", tempPatientId='" + tempPatientId + '\'' +
                ", extractedSeverity='" + extractedSeverity + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
