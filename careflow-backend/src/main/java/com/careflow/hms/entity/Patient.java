package com.careflow.hms.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Table(name = "patients")
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String patientId;

    @NotBlank
    private String name;

    @Min(0)
    @Max(150)
    private Integer age;

    private String gender;

    private String triageColor; // RED, YELLOW, GREEN

    @NotBlank
    @Size(max = 500)
    private String chiefComplaint;

    private Double weight;

    private String bloodPressure;

    private Integer heartRate;

    private Integer spO2;

    private String assignedDepartment;

    @ManyToOne
    @JoinColumn(name = "doctor_id")
    private Doctor assignedDoctor;

    private String assignedBedId;

    private String tempPatientId;

    private String visitType; // ADMITTED, CONSULTATION

    private String status; // WAITING, TRIAGE_COMPLETE, IN_CONSULTATION, DISCHARGED, CRITICAL

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public Patient() {
    }

    public Patient(Long id, String patientId, String name, Integer age, String gender, String triageColor, String chiefComplaint, Double weight, String bloodPressure, Integer heartRate, Integer spO2, String assignedDepartment, Doctor assignedDoctor, String assignedBedId, String tempPatientId, String visitType, String status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.patientId = patientId;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.triageColor = triageColor;
        this.chiefComplaint = chiefComplaint;
        this.weight = weight;
        this.bloodPressure = bloodPressure;
        this.heartRate = heartRate;
        this.spO2 = spO2;
        this.assignedDepartment = assignedDepartment;
        this.assignedDoctor = assignedDoctor;
        this.assignedBedId = assignedBedId;
        this.tempPatientId = tempPatientId;
        this.visitType = visitType;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getTriageColor() {
        return triageColor;
    }

    public void setTriageColor(String triageColor) {
        this.triageColor = triageColor;
    }

    public String getChiefComplaint() {
        return chiefComplaint;
    }

    public void setChiefComplaint(String chiefComplaint) {
        this.chiefComplaint = chiefComplaint;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public String getBloodPressure() {
        return bloodPressure;
    }

    public void setBloodPressure(String bloodPressure) {
        this.bloodPressure = bloodPressure;
    }

    public Integer getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(Integer heartRate) {
        this.heartRate = heartRate;
    }

    public Integer getSpO2() {
        return spO2;
    }

    public void setSpO2(Integer spO2) {
        this.spO2 = spO2;
    }

    public String getAssignedDepartment() {
        return assignedDepartment;
    }

    public void setAssignedDepartment(String assignedDepartment) {
        this.assignedDepartment = assignedDepartment;
    }

    public Doctor getAssignedDoctor() {
        return assignedDoctor;
    }

    public void setAssignedDoctor(Doctor assignedDoctor) {
        this.assignedDoctor = assignedDoctor;
    }

    public String getAssignedBedId() {
        return assignedBedId;
    }

    public void setAssignedBedId(String assignedBedId) {
        this.assignedBedId = assignedBedId;
    }

    public String getTempPatientId() {
        return tempPatientId;
    }

    public void setTempPatientId(String tempPatientId) {
        this.tempPatientId = tempPatientId;
    }

    public String getVisitType() {
        return visitType;
    }

    public void setVisitType(String visitType) {
        this.visitType = visitType;
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

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Patient{" +
                "id=" + id +
                ", patientId='" + patientId + '\'' +
                ", name='" + name + '\'' +
                ", triageColor='" + triageColor + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
