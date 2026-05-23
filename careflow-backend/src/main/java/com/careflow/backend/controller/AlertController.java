package com.careflow.backend.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class AlertController {

    // Listens for messages sent to /app/alert
    @MessageMapping("/alert")
    // Instantly broadcasts the returned payload to everyone subscribed to /topic/alerts
    @SendTo("/topic/alerts")
    public Object handleEmergencyOverride(Object payload) {
        System.out.println("🚨 RED ALERT RECEIVED FROM FRONTEND! Broadcasting to all dashboards...");
        return payload; 
    }
}