package com.orderManagement.order_service.services;

import com.orderManagement.order_service.client.ProductClient;
import com.orderManagement.order_service.model.Order;
import com.orderManagement.order_service.model.OrderItem;
import com.orderManagement.order_service.model.Product;
import com.orderManagement.order_service.model.dto.OrderItemRequest;
import com.orderManagement.order_service.model.dto.OrderItemResponse;
import com.orderManagement.order_service.model.dto.OrderRequest;
import com.orderManagement.order_service.model.dto.OrderResponse;
import com.orderManagement.order_service.repo.OrderRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.util.UUID;

@Service
public class OrderService {
    @Autowired
    private ProductClient productClient;
    @Autowired
    private OrderRepo orderRepo;

    public OrderResponse placeOrder(OrderRequest request) {

        Order order = new Order();
        String orderId = "ORD" + UUID.randomUUID().toString().substring(0,8).toUpperCase();
        order.setOrderId(orderId);
        order.setCustomerName(request.customerName());
        order.setEmail(request.email());
        order.setStatus("PLACED");
        order.setOrderDate(LocalDate.now());

        List<OrderItem> orderItems = new ArrayList<>();
        for (OrderItemRequest itemReq : request.items()) {
            Product product = productClient.getProductById((long) itemReq.productId());

            if (product.getStockQuantity() < itemReq.quantity()) {
                throw new RuntimeException("Not enough stock for product: " + product.getName());
            }

            // ✅ Reduce stock via Feign client
            productClient.reduceStock((long) itemReq.productId(), itemReq.quantity());

            BigDecimal totalPrice = product.getPrice().multiply(BigDecimal.valueOf(itemReq.quantity()));

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
        Order savedOrder = orderRepo.save(order);

        List<OrderItemResponse> itemResponses = new ArrayList<>();
        for (OrderItem item : order.getOrderItems()) {
            OrderItemResponse orderItemResponse = new OrderItemResponse(
                    item.getProductName(),
                    item.getQuantity(),
                    item.getTotalPrice()
            );

            itemResponses.add(orderItemResponse);
        }

        OrderResponse orderResponse = new OrderResponse(
                savedOrder.getOrderId(),
                savedOrder.getCustomerName(),
                savedOrder.getEmail(),
                savedOrder.getStatus(),
                savedOrder.getOrderDate(),
                itemResponses
        );

        return orderResponse;
    }

    @Transactional
    public List<OrderResponse> getAllOrderResponses() {

        List<Order> orders = orderRepo.findAll();
        List<OrderResponse> orderResponses = new ArrayList<>();

        for (Order order : orders) {


            List<OrderItemResponse> itemResponses = new ArrayList<>();

            for(OrderItem item : order.getOrderItems()) {
                OrderItemResponse orderItemResponse = new OrderItemResponse(
                        item.getProductName(),
                        item.getQuantity(),
                        item.getTotalPrice()
                );

                itemResponses.add(orderItemResponse);

            }
            OrderResponse orderResponse = new OrderResponse(
                    order.getOrderId(),
                    order.getCustomerName(),
                    order.getEmail(),
                    order.getStatus(),
                    order.getOrderDate(),
                    itemResponses
            );
            orderResponses.add(orderResponse);
        }

        return orderResponses;
    }
}