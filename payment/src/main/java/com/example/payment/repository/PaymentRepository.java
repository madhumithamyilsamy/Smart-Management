package com.example.payment.repository;

import com.example.payment.Entity.Paymentvalue;
import com.example.payment.Entity.Paymentvalue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Paymentvalue, Long> {
    Optional<Paymentvalue> findByOrderId(String orderId);
}
