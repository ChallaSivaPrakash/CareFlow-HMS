package com.careflow.hms.model; 
 
 import com.careflow.hms.entity.Appointment; 
 
 public class AppointmentNotification { 
     private String type; 
     private Appointment appointment; 
 
     public AppointmentNotification() {} 
 
     public AppointmentNotification(String type, Appointment appointment) { 
         this.type = type; 
         this.appointment = appointment; 
     } 
 
     public String getType() { return type; } 
     public void setType(String type) { this.type = type; } 
     public Appointment getAppointment() { return appointment; } 
     public void setAppointment(Appointment appointment) { this.appointment = appointment; } 
 } 
