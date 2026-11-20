package com.example.bankapp;

public class CardModel {
    private Long id;
    private Long customerId;
    private String cardNumber;
    private String cardType;
    private String expiryDate;
    private String cvv;

    public Long getId() { return id; }
    public Long getCustomerId() { return customerId; }
    public String getCardNumber() { return cardNumber; }
    public String getCardType() { return cardType; }
    public String getExpiryDate() { return expiryDate; }
    public String getCvv() { return cvv; }

    public void setId(Long id) { this.id = id; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }
    public void setCardType(String cardType) { this.cardType = cardType; }
    public void setExpiryDate(String expiryDate) { this.expiryDate = expiryDate; }
    public void setCvv(String cvv) { this.cvv = cvv; }
}