package com.orderManagement.order_service.model.dto;

import java.math.BigDecimal;
public record OrderItemResponse(
        String productName,
        int quantity,
        BigDecimal totalPrice
) {}

