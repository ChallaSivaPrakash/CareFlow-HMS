package com.careflow.hms.service;

import com.careflow.hms.entity.Patient;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class WebSocketNotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketNotificationService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendEmergencyAlert(Patient patient) {
        Map<String, Object> alert = new HashMap<>();
        alert.put("patientId", patient.getPatientId());
        alert.put("patientName", patient.getName());
        alert.put("triageColor", patient.getTriageColor());
        alert.put("chiefComplaint", patient.getChiefComplaint());
        alert.put("timestamp", System.currentTimeMillis());

        messagingTemplate.convertAndSend("/topic/alerts", alert);
    }

    public void notifyDepartment(String department, String message) {
        messagingTemplate.convertAndSend("/topic/department." + department, message);
    }

    public void sendToUser(String userId, String destination, Object payload) {
        messagingTemplate.convertAndSendToUser(userId, destination, payload);
    }
}
