package com.careflow.hms.repository; 
 
 import com.careflow.hms.entity.Appointment; 
 import org.springframework.data.jpa.repository.JpaRepository; 
 import org.springframework.data.jpa.repository.Query; 
 import org.springframework.data.repository.query.Param; 
 
 import java.time.LocalDate; 
 import java.time.LocalDateTime; 
 import java.util.List; 
 
 public interface AppointmentRepository extends JpaRepository<Appointment, Long> { 
     List<Appointment> findByDoctorIdAndStartTimeBetween(Long doctorId, LocalDateTime start, LocalDateTime end); 
     
     @Query("SELECT a FROM Appointment a WHERE a.doctor.id = :doctorId AND FUNCTION('DATE', a.startTime) = :date") 
     List<Appointment> findByDoctorIdAndDate(@Param("doctorId") Long doctorId, @Param("date") LocalDate date); 
     
     List<Appointment> findByDoctorIdOrderByStartTimeDesc(Long doctorId); 
     List<Appointment> findByPatientIdOrderByStartTimeDesc(Long patientId); 
 } 
