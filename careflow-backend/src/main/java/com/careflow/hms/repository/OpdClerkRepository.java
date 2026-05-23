package com.careflow.hms.repository;

import com.careflow.hms.entity.OpdClerk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OpdClerkRepository extends JpaRepository<OpdClerk, Long> {
    List<OpdClerk> findByIsActiveTrue();
}
