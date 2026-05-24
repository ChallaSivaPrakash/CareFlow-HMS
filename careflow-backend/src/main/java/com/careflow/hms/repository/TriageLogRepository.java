package com.careflow.hms.repository;

import com.careflow.hms.entity.TriageLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TriageLogRepository extends JpaRepository<TriageLog, Long> {
    Optional<TriageLog> findByTempPatientId(String tempPatientId);
    List<TriageLog> findByStatus(String status);
}
