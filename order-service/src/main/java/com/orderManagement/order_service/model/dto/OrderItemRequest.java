package com.orderManagement.order_service.model.dto;

public record OrderItemRequest(
        int productId,int quantity) {}
