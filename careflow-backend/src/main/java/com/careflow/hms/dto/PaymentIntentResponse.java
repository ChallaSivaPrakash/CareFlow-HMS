package com.careflow.hms.dto;

public class PaymentIntentResponse {
    private String clientSecret;
    private String id;

    public PaymentIntentResponse() {}
    public PaymentIntentResponse(String clientSecret, String id) {
        this.clientSecret = clientSecret;
        this.id = id;
    }

    public String getClientSecret() { return clientSecret; }
    public void setClientSecret(String clientSecret) { this.clientSecret = clientSecret; }
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
}
