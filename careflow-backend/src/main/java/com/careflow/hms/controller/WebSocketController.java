package com.careflow.hms.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import java.util.Map;

@Controller
public class WebSocketController {

    @MessageMapping("/emergency.trigger")
    @SendTo("/topic/alerts")
    public Map<String, Object> triggerEmergency(Map<String, Object> payload) {
        return payload;
    }
}
