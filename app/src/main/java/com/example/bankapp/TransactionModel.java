package com.example.bankapp;

public class TransactionModel {
    private Long id;
    private Long customerId;
    private String type;
    private Double amount;
    private String description;
    private String dateOperation;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDateOperation() { return dateOperation; }
    public void setDateOperation(String dateOperation) { this.dateOperation = dateOperation; }
}