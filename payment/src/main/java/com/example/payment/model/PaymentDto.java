package com.example.payment.model;

public class PaymentDto {

    private Long paymentId;
    private String orderId;
    private String paymentMode;
    private Double amount;
    private String status;
    private String message;

    public PaymentDto() {}

    public PaymentDto(Long paymentId, String orderId, String paymentMode, Double amount, String status, String message) {
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.paymentMode = paymentMode;
        this.amount = amount;
        this.status = status;
        this.message = message;
    }

    // Getters and Setters
    public Long getPaymentId() { return paymentId; }
    public void setPaymentId(Long paymentId) { this.paymentId = paymentId; }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getPaymentMode() { return paymentMode; }
    public void setPaymentMode(String paymentMode) { this.paymentMode = paymentMode; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
