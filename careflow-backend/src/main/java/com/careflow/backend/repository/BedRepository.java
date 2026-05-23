package com.careflow.backend.repository;

import com.careflow.backend.entity.Bed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BedRepository extends JpaRepository<Bed, Long> {
    List<Bed> findByIsOccupiedFalse();
    List<Bed> findByBedTypeAndIsOccupiedFalse(String bedType);
}
