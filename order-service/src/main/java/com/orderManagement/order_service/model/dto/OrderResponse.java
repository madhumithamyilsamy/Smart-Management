package com.orderManagement.order_service.model.dto;

import java.time.LocalDate;
import java.util.List;

public record OrderResponse(
        String orderId,
        String customerName,
        String email,
        String status,
        LocalDate orderDate,
        String paymentStatus,
        List<OrderItemResponse> items
) { }
