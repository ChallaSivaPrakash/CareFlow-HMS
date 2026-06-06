package com.careflow.hms.controller; 
 
 import com.careflow.hms.dto.AppointmentRequest; 
 import com.careflow.hms.entity.Appointment; 
 import com.careflow.hms.exception.ConcurrentBookingException; 
 import com.careflow.hms.exception.SlotUnavailableException; 
 import com.careflow.hms.service.AppointmentService; 
 import com.careflow.hms.service.NotificationService; 
 import com.careflow.hms.model.AppointmentNotification; 
 import org.springframework.http.HttpStatus; 
 import org.springframework.http.ResponseEntity; 
 import org.springframework.web.bind.annotation.*; 
 
 import java.time.LocalDate; 
 import java.time.LocalDateTime; 
 import java.util.List; 
 
 @RestController 
 @RequestMapping("/api/appointments") 
 @CrossOrigin(origins = "http://localhost:4200") 
 public class AppointmentController { 
 
     private final AppointmentService appointmentService; 
     private final NotificationService notificationService; 
 
     public AppointmentController(AppointmentService appointmentService, NotificationService notificationService) { 
         this.appointmentService = appointmentService; 
         this.notificationService = notificationService; 
     } 
 
     @GetMapping("/doctor/{doctorId}/slots") 
     public ResponseEntity<List<LocalDateTime>> getAvailableSlots( 
             @PathVariable Long doctorId, 
             @RequestParam @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) LocalDate date) { 
         return ResponseEntity.ok(appointmentService.getAvailableSlots(doctorId, date)); 
     } 
 
     @PostMapping("/book") 
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
     public ResponseEntity<List<Appointment>> getDoctorAppointments(@PathVariable Long doctorId) { 
         return ResponseEntity.ok(appointmentService.getAppointmentsByDoctor(doctorId)); 
     } 
 
     @GetMapping("/patient/{patientId}") 
     public ResponseEntity<List<Appointment>> getPatientAppointments(@PathVariable Long patientId) { 
         return ResponseEntity.ok(appointmentService.getAppointmentsByPatient(patientId)); 
     } 
 } 
