package com.careflow.hms.model; 
 
 public class RecoveryIndex { 
     private Long patientId; 
     private String patientName; 
     private int score; 
     private String trend; 
 
     public RecoveryIndex() {} 
 
     public Long getPatientId() { return patientId; } 
     public void setPatientId(Long patientId) { this.patientId = patientId; } 
     public String getPatientName() { return patientName; } 
     public void setPatientName(String patientName) { this.patientName = patientName; } 
     public int getScore() { return score; } 
     public void setScore(int score) { this.score = score; } 
     public String getTrend() { return trend; } 
     public void setTrend(String trend) { this.trend = trend; } 
 } 
