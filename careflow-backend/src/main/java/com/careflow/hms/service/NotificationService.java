package com.careflow.hms.service; 
 
 import org.springframework.mail.SimpleMailMessage; 
 import org.springframework.mail.javamail.JavaMailSender; 
 import org.springframework.scheduling.annotation.Async; 
 import org.springframework.stereotype.Service; 
 
 @Service 
 public class NotificationService { 
     private final JavaMailSender mailSender; 
     private final WebSocketNotificationService webSocketNotificationService; 
 
     public NotificationService(JavaMailSender mailSender, WebSocketNotificationService webSocketNotificationService) { 
         this.mailSender = mailSender; 
         this.webSocketNotificationService = webSocketNotificationService; 
     } 
 
     public void sendWebSocketNotification(String userId, String destination, Object payload) {
         webSocketNotificationService.sendToUser(userId, destination, payload);
     }

     @Async 
     public void sendEmail(String to, String subject, String body) { 
         SimpleMailMessage message = new SimpleMailMessage(); 
         message.setTo(to); 
         message.setSubject(subject); 
         message.setText(body); 
         mailSender.send(message); 
     } 
 } 
