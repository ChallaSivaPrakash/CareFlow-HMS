package com.careflow.hms.service;

import com.careflow.hms.dto.PaymentIntentResponse;
import com.careflow.hms.model.PaymentEvent;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    public PaymentIntentResponse createPaymentIntent(Long amount, String currency) {
        // Mock implementation
        return new PaymentIntentResponse("mock_client_secret", "pi_mock_id");
    }

    public PaymentEvent verifyWebhook(String payload, String sigHeader) {
        // Mock implementation
        return new PaymentEvent("payment_intent.succeeded", "mock_user_id");
    }
}
