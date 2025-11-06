package com.orderManagement.order_service.client;

import com.orderManagement.order_service.model.dto.PaymentRequest;
import com.orderManagement.order_service.model.dto.PaymentResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "payment-service", url = "http://localhost:8081/api/payments")
public interface PaymentClient {

    @PostMapping
    PaymentResponse makePayment(@RequestBody PaymentRequest paymentRequest);
}
