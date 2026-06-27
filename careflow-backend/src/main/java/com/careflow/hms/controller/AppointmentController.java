package com.careflow.hms.controller; 

import com.careflow.hms.dto.AppointmentRequest;
import com.careflow.hms.dto.BookingAgentRequest;
import com.careflow.hms.dto.DoctorAvailability;
import com.careflow.hms.entity.Appointment; 
import com.careflow.hms.entity.Patient;
import com.careflow.hms.exception.ConcurrentBookingException; 
import com.careflow.hms.exception.SlotUnavailableException; 
import com.careflow.hms.repository.PatientRepository;
import com.careflow.hms.service.AppointmentService; 
import com.careflow.hms.service.NotificationService; 
import com.careflow.hms.model.AppointmentNotification; 
import org.springframework.http.HttpStatus; 
import org.springframework.http.ResponseEntity; 
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*; 

import java.time.LocalDate; 
import java.time.LocalDateTime; 
import java.util.List; 

@RestController 
@RequestMapping("/api/appointments") 
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true") 
public class AppointmentController { 

    private final AppointmentService appointmentService; 
    private final NotificationService notificationService; 
    private final PatientRepository patientRepository;

    public AppointmentController(AppointmentService appointmentService, NotificationService notificationService, PatientRepository patientRepository) { 
        this.appointmentService = appointmentService; 
        this.notificationService = notificationService; 
        this.patientRepository = patientRepository;
    }

    // Get current patient's appointments
    @GetMapping("/me")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<List<Appointment>> getCurrentPatientAppointments() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        // For now, return first patient's appointments
        Patient patient = patientRepository.findAll().stream().findFirst().orElse(null);
        if (patient == null) {
            return ResponseEntity.ok(List.of());
        }
        return ResponseEntity.ok(appointmentService.getAppointmentsByPatient(patient.getId()));
    } 

    // New: Get availability by department and date for AI agent
    @GetMapping("/availability")
    public ResponseEntity<List<DoctorAvailability>> getAvailabilityByDepartment(
            @RequestParam String department,
            @RequestParam @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(appointmentService.getDoctorAvailabilityByDepartment(department, date));
    }

    // New: Book appointment via AI agent
    @PostMapping("/agent/book")
    public ResponseEntity<?> bookAppointmentViaAgent(@RequestBody BookingAgentRequest request) {
        try {
            Appointment appointment = appointmentService.bookAppointmentForAgent(request);
            
            return ResponseEntity.ok(appointment);
        } catch (SlotUnavailableException | ConcurrentBookingException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @GetMapping("/doctor/{doctorId}/slots") 
    @PreAuthorize("hasAnyRole('ADMIN', 'OPD_CLERK', 'CLERK', 'DOCTOR')") 
    public ResponseEntity<List<LocalDateTime>> getAvailableSlots( 
            @PathVariable Long doctorId, 
            @RequestParam @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) LocalDate date) { 
        return ResponseEntity.ok(appointmentService.getAvailableSlots(doctorId, date)); 
    } 

    @PostMapping("/book") 
    @PreAuthorize("hasAnyRole('ADMIN', 'OPD_CLERK', 'CLERK')") 
    public ResponseEntity<?> bookAppointment(@RequestBody AppointmentRequest request) { 
        try { 
            Appointment appointment = appointmentService.bookAppointment(request); 
            
            // Email patient 
            notificationService.sendEmail( 
                appointment.getPatient().getName() + "@example.com", // Replace with real email mapping if available 
                "Appointment Confirmation - CareFlow HMS", 
                "Your appointment with Dr. " + appointment.getDoctor().getName() + 
                " on " + appointment.getStartTime().toString() + " is confirmed." 
            ); 
            
            // WebSocket Doctor Notification 
            notificationService.sendWebSocketNotification( 
                String.valueOf(appointment.getDoctor().getId()), 
                "/queue/appointments", 
                new AppointmentNotification("NEW_BOOKING", appointment) 
            ); 
            
            return ResponseEntity.ok(appointment); 
        } catch (SlotUnavailableException | ConcurrentBookingException e) { 
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage()); 
        } 
    } 

    @GetMapping("/doctor/{doctorId}") 
    @PreAuthorize("hasAnyRole('ADMIN', 'OPD_CLERK', 'CLERK', 'DOCTOR')") 
    public ResponseEntity<List<Appointment>> getDoctorAppointments(@PathVariable Long doctorId) { 
        return ResponseEntity.ok(appointmentService.getAppointmentsByDoctor(doctorId)); 
    } 

    @GetMapping("/patient/{patientId}") 
    @PreAuthorize("hasAnyRole('ADMIN', 'OPD_CLERK', 'CLERK', 'DOCTOR')") 
    public ResponseEntity<List<Appointment>> getPatientAppointments(@PathVariable Long patientId) { 
        return ResponseEntity.ok(appointmentService.getAppointmentsByPatient(patientId)); 
    } 
} 
