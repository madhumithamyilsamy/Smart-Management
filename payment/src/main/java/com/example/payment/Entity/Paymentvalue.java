package com.example.payment.Entity;

import jakarta.persistence.*;

@Entity
@Table(name="payment_entity")
public class Paymentvalue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String orderId;
    private String paymentMode;
    private Double amount;
    private String status;
    private String message;

    public Paymentvalue() {}

    public Paymentvalue(String orderId, String paymentMode, Double amount, String status) {
        this.orderId = orderId;
        this.paymentMode = paymentMode;
        this.amount = amount;
        this.status = status;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getPaymentMode() { return paymentMode; }
    public void setPaymentMode(String paymentMode) { this.paymentMode = paymentMode; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public void setMessage(String message) {
        this.message= message;
    }

    public String getMessage() {
        return message;
    }
}
