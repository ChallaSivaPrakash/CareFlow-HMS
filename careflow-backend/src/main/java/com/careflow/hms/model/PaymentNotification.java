package com.careflow.hms.model;

public class PaymentNotification {
    private String message;
    private PaymentEvent event;

    public PaymentNotification() {}
    public PaymentNotification(String message, PaymentEvent event) {
        this.message = message;
        this.event = event;
    }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public PaymentEvent getEvent() { return event; }
    public void setEvent(PaymentEvent event) { this.event = event; }
}
