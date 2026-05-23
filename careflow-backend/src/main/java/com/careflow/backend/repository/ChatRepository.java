package com.careflow.backend.repository;

import com.careflow.backend.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findTop50ByOrderByTimestampDesc();
    List<ChatMessage> findTop50ByDepartmentOrderByTimestampDesc(String department);
}
