package com.careflow.hms.entity; 
 
 import jakarta.persistence.*; 
 
 @Entity 
 @Table(name = "beds") 
 public class Bed { 
 
     @Id 
     @GeneratedValue(strategy = GenerationType.IDENTITY) 
     private Long id; 
 
     @Column(nullable = false) 
     private String bedNumber; 
 
     private String ward; 
 
     @Column(nullable = false) 
     private boolean occupied = false; 
 
     @Column(name = "patient_id") 
     private Long patientId; 
 
     public Bed() {} 
 
     public Long getId() { return id; } 
     public void setId(Long id) { this.id = id; } 
     public String getBedNumber() { return bedNumber; } 
     public void setBedNumber(String bedNumber) { this.bedNumber = bedNumber; } 
     public String getWard() { return ward; } 
     public void setWard(String ward) { this.ward = ward; } 
     public boolean isOccupied() { return occupied; } 
     public void setOccupied(boolean occupied) { this.occupied = occupied; } 
     public Long getPatientId() { return patientId; } 
     public void setPatientId(Long patientId) { this.patientId = patientId; } 
 } 
