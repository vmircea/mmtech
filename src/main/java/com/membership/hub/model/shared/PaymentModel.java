package com.membership.hub.model.shared;

import java.time.LocalDate;

public class PaymentModel {
    private String senderId;
    private String receiverId;
    private Double amount;
    private LocalDate date;
    private String description;

    public PaymentModel(String senderId, String receiverId, Double amount, LocalDate date, String description) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.amount = amount;
        this.date = date;
        this.description = description;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public Double getAmount() {
        return amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }
}
