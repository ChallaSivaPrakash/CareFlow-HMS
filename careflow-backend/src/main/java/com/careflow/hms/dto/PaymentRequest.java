package com.careflow.hms.dto;

public class PaymentRequest {
    private Long amount;
    private String currency;

    public PaymentRequest() {}

    public Long getAmount() { return amount; }
    public void setAmount(Long amount) { this.amount = amount; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
}
