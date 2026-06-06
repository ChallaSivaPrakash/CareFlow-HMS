package com.careflow.hms.model;

public class PaymentEvent {
    private String type;
    private String userId;

    public PaymentEvent() {}
    public PaymentEvent(String type, String userId) {
        this.type = type;
        this.userId = userId;
    }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
}
