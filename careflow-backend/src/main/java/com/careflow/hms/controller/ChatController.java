package com.careflow.hms.controller;

import com.careflow.hms.entity.ChatMessage;
// Fixed: Added correct import for ChatRepository (verified package path in repository module)
import com.careflow.hms.repository.ChatRepository;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@RestController
public class ChatController {

    private final ChatRepository chatRepository;
    private final SimpMessagingTemplate messagingTemplate; // The STOMP Router

    public ChatController(ChatRepository chatRepository, SimpMessagingTemplate messagingTemplate) {
        this.chatRepository = chatRepository;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/chat.sendToDepartment")
    public void sendMessage(ChatMessage chatMessage) {
        if (chatMessage.getTimestamp() == null) {
            chatMessage.setTimestamp(LocalDateTime.now());
        }
        // Save to DB
        chatRepository.save(chatMessage);
        
        // Dynamically route the message to the specific department channel
        String destination = "/topic/department." + chatMessage.getDepartment();
        messagingTemplate.convertAndSend(destination, chatMessage);
    }

    @GetMapping("/api/chat/history/{department}")
    public List<ChatMessage> getChatHistory(@PathVariable("department") String department) {
        List<ChatMessage> history = chatRepository.findTop50ByDepartmentOrderByTimestampDesc(department);
        Collections.reverse(history);
        return history;
    }
}