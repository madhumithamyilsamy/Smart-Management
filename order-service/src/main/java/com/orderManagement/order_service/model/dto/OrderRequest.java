package com.orderManagement.order_service.model.dto;

import java.util.List;
public record OrderRequest(
        String customerName,
        String email,
        String paymentMode,
        List<OrderItemRequest> items ) {
}
