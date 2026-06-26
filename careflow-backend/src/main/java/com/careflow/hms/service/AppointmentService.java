package com.careflow.hms.service;

import com.careflow.hms.dto.AppointmentRequest;
import com.careflow.hms.dto.BookingAgentRequest;
import com.careflow.hms.dto.DoctorAvailability;
import com.careflow.hms.entity.Appointment;
import com.careflow.hms.entity.Doctor;
import com.careflow.hms.entity.Patient;
import com.careflow.hms.exception.ConcurrentBookingException;
import com.careflow.hms.exception.SlotUnavailableException;
import com.careflow.hms.repository.AppointmentRepository;
import com.careflow.hms.repository.DoctorRepository;
import com.careflow.hms.repository.PatientRepository;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service 
public class AppointmentService { 
    private static final int MAX_RETRIES = 3; 
    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    public AppointmentService(AppointmentRepository appointmentRepository, PatientRepository patientRepository, DoctorRepository doctorRepository) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
    }

    public List<LocalDateTime> getAvailableSlots(Long doctorId, LocalDate date) { 
        List<LocalDateTime> allSlots = new java.util.ArrayList<>(); 
        LocalDateTime startOfDay = date.atTime(9, 0); 
        LocalDateTime endOfDay = date.atTime(17, 0); 
        
        for (LocalDateTime time = startOfDay; time.isBefore(endOfDay); time = time.plusMinutes(30)) { 
            allSlots.add(time); 
        } 
        
        List<Appointment> booked = appointmentRepository.findByDoctorIdAndDate(doctorId, date); 
        for (Appointment apt : booked) { 
            allSlots.removeIf(slot -> 
                slot.equals(apt.getStartTime()) || 
                (slot.isAfter(apt.getStartTime()) && slot.isBefore(apt.getEndTime())) 
            ); 
        } 
        
        return allSlots; 
    } 

    // New: Get availability for all doctors in a department
    public List<DoctorAvailability> getDoctorAvailabilityByDepartment(String department, LocalDate date) {
        List<Doctor> doctors = doctorRepository.findByDepartment(department);
        List<DoctorAvailability> availabilityList = new ArrayList<>();

        for (Doctor doctor : doctors) {
            List<LocalDateTime> availableDateTimeSlots = getAvailableSlots(doctor.getId(), date);
            List<LocalTime> availableTimes = availableDateTimeSlots.stream()
                .map(LocalDateTime::toLocalTime)
                .collect(Collectors.toList());

            availabilityList.add(new DoctorAvailability(
                doctor.getId(),
                doctor.getName(),
                doctor.getSpecialty(),
                doctor.getDepartment(),
                availableTimes
            ));
        }

        return availabilityList;
    }

    // New: Simplified booking for AI agent (creates a temporary patient if needed)
    public Appointment bookAppointmentForAgent(BookingAgentRequest request) {
        ObjectOptimisticLockingFailureException lastException = null;
        LocalDateTime startTime = request.getAppointmentDate().atTime(request.getAppointmentTime());
        LocalDateTime endTime = startTime.plusMinutes(30);

        for (int attempt = 0; attempt < MAX_RETRIES; attempt++) {
            try {
                // Check slot availability
                List<Appointment> existing = appointmentRepository
                    .findByDoctorIdAndStartTimeBetween(request.getDoctorId(), startTime, endTime);
                if (!existing.isEmpty()) {
                    throw new SlotUnavailableException("Time slot already booked");
                }

                // Find or create a simple patient for the agent
                Patient patient = patientRepository.findAll().stream()
                    .filter(p -> p.getName() != null && p.getName().equalsIgnoreCase(request.getPatientName()))
                    .findFirst()
                    .orElseGet(() -> {
                        Patient newPatient = new Patient();
                        newPatient.setName(request.getPatientName());
                        newPatient.setChiefComplaint("Appointment booking via AI agent");
                        newPatient.setStatus("WAITING");
                        newPatient.setTriageColor("GREEN");
                        return patientRepository.save(newPatient);
                    });

                Doctor doctor = doctorRepository.findById(request.getDoctorId())
                        .orElseThrow(() -> new RuntimeException("Doctor not found"));

                Appointment appointment = new Appointment();
                appointment.setPatient(patient);
                appointment.setDoctor(doctor);
                appointment.setStartTime(startTime);
                appointment.setEndTime(endTime);

                return appointmentRepository.save(appointment);
            } catch (ObjectOptimisticLockingFailureException e) {
                lastException = e;
                // Reload and retry
                try {
                    Thread.sleep(100 * (attempt + 1)); // progressive delay
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        throw new ConcurrentBookingException("Booking failed due to concurrent access. Please try again.", lastException);
    }

    public List<Appointment> getAppointmentsByDoctor(Long doctorId) { 
        return appointmentRepository.findByDoctorIdOrderByStartTimeDesc(doctorId); 
    } 

    public List<Appointment> getAppointmentsByPatient(Long patientId) { 
        return appointmentRepository.findByPatientIdOrderByStartTimeDesc(patientId); 
    } 

    public Appointment bookAppointment(AppointmentRequest request) { 
        ObjectOptimisticLockingFailureException lastException = null; 
        for (int attempt = 0; attempt < MAX_RETRIES; attempt++) { 
            try { 
                // Check slot availability 
                List<Appointment> existing = appointmentRepository 
                    .findByDoctorIdAndStartTimeBetween(request.getDoctorId(), request.getStartTime(), request.getEndTime()); 
                if (!existing.isEmpty()) { 
                    throw new SlotUnavailableException("Time slot already booked"); 
                } 
                
                Patient patient = patientRepository.findById(request.getPatientId())
                        .orElseThrow(() -> new RuntimeException("Patient not found"));
                Doctor doctor = doctorRepository.findById(request.getDoctorId())
                        .orElseThrow(() -> new RuntimeException("Doctor not found"));

                Appointment appointment = new Appointment();
                appointment.setPatient(patient);
                appointment.setDoctor(doctor);
                appointment.setStartTime(request.getStartTime());
                appointment.setEndTime(request.getEndTime());

                return appointmentRepository.save(appointment); 
            } catch (ObjectOptimisticLockingFailureException e) { 
                lastException = e; 
                // Reload and retry 
                try { 
                    Thread.sleep(100 * (attempt + 1)); // progressive delay 
                } catch (InterruptedException ie) { 
                    Thread.currentThread().interrupt(); 
                } 
            } 
        } 
        throw new ConcurrentBookingException("Booking failed due to concurrent access. Please try again.", lastException); 
    } 
} 
