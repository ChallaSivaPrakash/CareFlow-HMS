package com.careflow.hms.controller;

import com.careflow.hms.dto.PaymentIntentResponse;
import com.careflow.hms.dto.PaymentRequest;
import com.careflow.hms.model.PaymentEvent;
import com.careflow.hms.model.PaymentNotification;
import com.careflow.hms.service.NotificationService;
import com.careflow.hms.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController 
@RequestMapping("/api/payments") 
@CrossOrigin(origins = "http://localhost:4200") 
public class PaymentController { 
    private final PaymentService paymentService; 
    private final NotificationService notificationService; 

    public PaymentController(PaymentService paymentService, NotificationService notificationService) {
        this.paymentService = paymentService;
        this.notificationService = notificationService;
    }
 
    @PostMapping("/create-payment-intent") 
    public ResponseEntity<PaymentIntentResponse> createPaymentIntent(@RequestBody PaymentRequest request) { 
        PaymentIntentResponse response = paymentService.createPaymentIntent(request.getAmount(), request.getCurrency()); 
        return ResponseEntity.ok(response); 
    } 
 
    @PostMapping("/webhook") 
    public ResponseEntity<String> handleStripeWebhook( 
        @RequestBody String payload, 
        @RequestHeader("Stripe-Signature") String sigHeader) { 
        PaymentEvent event = paymentService.verifyWebhook(payload, sigHeader); 
        if ("payment_intent.succeeded".equals(event.getType())) { 
            notificationService.sendWebSocketNotification( 
                event.getUserId(), "/queue/payments", 
                new PaymentNotification("Payment successful", event) 
            ); 
        } 
        return ResponseEntity.ok(""); 
    } 
} 
