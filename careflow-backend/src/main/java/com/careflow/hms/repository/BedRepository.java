package com.careflow.hms.repository; 
 
 import com.careflow.hms.entity.Bed; 
 import org.springframework.data.jpa.repository.JpaRepository; 
 
 import java.util.List; 
 
 public interface BedRepository extends JpaRepository<Bed, Long> { 
     List<Bed> findByWard(String ward); 
     List<Bed> findByOccupied(boolean occupied); 
 } 
