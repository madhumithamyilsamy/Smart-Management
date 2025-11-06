package com.orderManagement.order_service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResponse {
    private Long paymentId;
    private String orderId;
    private String paymentMode;
    private Double amount;
    private String status;
    private String message;
}
