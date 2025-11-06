package com.orderManagement.order_service.client;

import com.orderManagement.order_service.model.Product;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "product-service", url = "http://localhost:8080/api")
public interface ProductClient {

    @GetMapping("/product/{id}")
    Product getProductById(@PathVariable Long id);
    @PutMapping("/product/{id}/reduceStock")
    void reduceStock(@PathVariable("id") Long id, @RequestParam("quantity") int quantity);

}
