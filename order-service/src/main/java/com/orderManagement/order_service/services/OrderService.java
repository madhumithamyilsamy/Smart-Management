package com.orderManagement.order_service.services;

import com.orderManagement.order_service.client.ProductClient;
import com.orderManagement.order_service.client.PaymentClient;
import com.orderManagement.order_service.model.Order;
import com.orderManagement.order_service.model.OrderItem;
import com.orderManagement.order_service.model.Product;
import com.orderManagement.order_service.model.dto.*;
import com.orderManagement.order_service.repo.OrderRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    @Autowired
    private ProductClient productClient;

    @Autowired
    private PaymentClient paymentClient;

    @Autowired
    private OrderRepo orderRepo;

    @Transactional
    public OrderResponse placeOrder(OrderRequest request) {

        // Create order object
        Order order = new Order();
        String orderId = "ORD" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        order.setOrderId(orderId);
        order.setCustomerName(request.customerName());
        order.setEmail(request.email());
        order.setStatus("PLACED");
        order.setOrderDate(LocalDate.now());
        order.setPaymentMode(request.paymentMode());

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        // Loop through products
        for (OrderItemRequest itemReq : request.items()) {
            Product product = productClient.getProductById((long) itemReq.productId());

            if (product.getStockQuantity() < itemReq.quantity()) {
                throw new RuntimeException("Not enough stock for product: " + product.getName());
            }

            // Reduce stock in product-service
            productClient.reduceStock((long) itemReq.productId(), itemReq.quantity());

            BigDecimal totalPrice = product.getPrice().multiply(BigDecimal.valueOf(itemReq.quantity()));
            totalAmount = totalAmount.add(totalPrice);

            OrderItem orderItem = OrderItem.builder()
                    .productId((long) product.getId())
                    .productName(product.getName())
                    .quantity(itemReq.quantity())
                    .totalPrice(totalPrice)
                    .order(order)
                    .build();

            orderItems.add(orderItem);
        }

        order.setOrderItems(orderItems);
        order.setTotalAmount(totalAmount);

        //Save order first
        Order savedOrder = orderRepo.save(order);

        //Prepare payment request
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setOrderId(order.getOrderId());
        paymentRequest.setPaymentMode(request.paymentMode());
        paymentRequest.setAmount(totalAmount.doubleValue());

        // Call payment-service via Feign Client
        PaymentResponse paymentResponse = paymentClient.makePayment(paymentRequest);

        // Update order payment status based on payment result
        if ("SUCCESS".equalsIgnoreCase(paymentResponse.getStatus())) {
            savedOrder.setPaymentStatus("PAID");
        } else if ("PENDING".equalsIgnoreCase(paymentResponse.getStatus())) {
            savedOrder.setPaymentStatus("PENDING");
        } else {
            savedOrder.setPaymentStatus("FAILED");
        }

        orderRepo.save(savedOrder);

        // Prepare response
        List<OrderItemResponse> itemResponses = new ArrayList<>();
        for (OrderItem item : savedOrder.getOrderItems()) {
            OrderItemResponse itemResponse = new OrderItemResponse(
                    item.getProductName(),
                    item.getQuantity(),
                    item.getTotalPrice()
            );
            itemResponses.add(itemResponse);
        }

        return new OrderResponse(
                savedOrder.getOrderId(),
                savedOrder.getCustomerName(),
                savedOrder.getEmail(),
                savedOrder.getStatus(),
                savedOrder.getOrderDate(),
                savedOrder.getPaymentStatus(),
                itemResponses
        );
    }

    @Transactional
    public List<OrderResponse> getAllOrderResponses() {

        List<Order> orders = orderRepo.findAll();
        List<OrderResponse> orderResponses = new ArrayList<>();

        for (Order order : orders) {
            List<OrderItemResponse> itemResponses = new ArrayList<>();

            for (OrderItem item : order.getOrderItems()) {
                itemResponses.add(new OrderItemResponse(
                        item.getProductName(),
                        item.getQuantity(),
                        item.getTotalPrice()
                ));
            }

            orderResponses.add(new OrderResponse(
                    order.getOrderId(),
                    order.getCustomerName(),
                    order.getEmail(),
                    order.getStatus(),
                    order.getOrderDate(),
                    order.getPaymentStatus(),
                    itemResponses
            ));
        }

        return orderResponses;
    }
}
