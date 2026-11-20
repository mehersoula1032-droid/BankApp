package com.example.bankapp;

public class NotificationModel {
    private Long id;
    private Long customerId;
    private String message;
    private String dateNotification;
    private String email; // ✅ زدنا هذي

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getDateNotification() { return dateNotification; }
    public void setDateNotification(String dateNotification) { this.dateNotification = dateNotification; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}