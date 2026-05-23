package com.careflow.backend.service;

import com.careflow.backend.entity.PatientRecord;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebSocketNotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketNotificationService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendEmergencyAlert(PatientRecord patient) {
        messagingTemplate.convertAndSend("/topic/emergencies", patient);
    }
}
