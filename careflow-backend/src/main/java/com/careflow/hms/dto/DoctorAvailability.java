package com.careflow.hms.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class DoctorAvailability {
    private Long doctorId;
    private String doctorName;
    private String specialty;
    private String department;
    private List<LocalTime> availableSlots;

    public DoctorAvailability() {}

    public DoctorAvailability(Long doctorId, String doctorName, String specialty, String department, List<LocalTime> availableSlots) {
        this.doctorId = doctorId;
        this.doctorName = doctorName;
        this.specialty = specialty;
        this.department = department;
        this.availableSlots = availableSlots;
    }

    public Long getDoctorId() { return doctorId; }
    public void setDoctorId(Long doctorId) { this.doctorId = doctorId; }
    public String getDoctorName() { return doctorName; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }
    public String getSpecialty() { return specialty; }
    public void setSpecialty(String specialty) { this.specialty = specialty; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public List<LocalTime> getAvailableSlots() { return availableSlots; }
    public void setAvailableSlots(List<LocalTime> availableSlots) { this.availableSlots = availableSlots; }
}
