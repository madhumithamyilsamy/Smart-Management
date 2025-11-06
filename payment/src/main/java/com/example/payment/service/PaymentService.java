
package com.example.payment.service;

import com.example.payment.Entity.Paymentvalue;
import com.example.payment.model.PaymentDto;
import com.example.payment.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;
    private boolean isPaid;
    public boolean isPaid() {
        return isPaid;
    }

    public void setPaid(boolean isPaid) {
        this.isPaid = isPaid;
    }
    public PaymentDto makePayment(PaymentDto dto) {

        Paymentvalue payment = new Paymentvalue();
        payment.setOrderId(dto.getOrderId());
        payment.setPaymentMode(dto.getPaymentMode());
        payment.setAmount(dto.getAmount());

        // Handle invalid or empty amount
        if (dto.getAmount() == null || dto.getAmount() <= 0) {
            payment.setStatus("FAILED");
            payment.setMessage("Payment failed: Invalid amount");
        }
        // Handle Cash on Delivery (COD)
        else if (dto.getPaymentMode().equalsIgnoreCase("CASH")) {
            payment.setStatus("PENDING");
            payment.setMessage("Cash on Delivery - Payment pending");
        }
        // Handle valid online payments
        else if (dto.getPaymentMode().equalsIgnoreCase("GPAY") ||
                dto.getPaymentMode().equalsIgnoreCase("UPI") ||
                dto.getPaymentMode().equalsIgnoreCase("CARD") ||
                dto.getPaymentMode().equalsIgnoreCase("PAYTM")) {
            payment.setStatus("SUCCESS");
            payment.setMessage("Payment processed successfully");
        }
        // Handle invalid modes
        else {
            payment.setStatus("FAILED");
            payment.setMessage("Invalid payment mode");
        }

        // Save the transaction
        Paymentvalue savedPayment = paymentRepository.save(payment);

        // Prepare response
        PaymentDto response = new PaymentDto();
        response.setPaymentId(savedPayment.getId());
        response.setOrderId(savedPayment.getOrderId());
        response.setPaymentMode(savedPayment.getPaymentMode());
        response.setAmount(savedPayment.getAmount());
        response.setStatus(savedPayment.getStatus());
        response.setMessage(savedPayment.getMessage());

        return response;
    }

    public PaymentDto getPayment(Long id) {
        Optional<Paymentvalue> optionalPayment = paymentRepository.findById(id);

        if (optionalPayment.isEmpty()) return null;

        Paymentvalue payment = optionalPayment.get();
        PaymentDto response = new PaymentDto();
        response.setPaymentId(payment.getId());
        response.setOrderId(payment.getOrderId());
        response.setPaymentMode(payment.getPaymentMode());
        response.setAmount(payment.getAmount());
        response.setStatus(payment.getStatus());
        response.setMessage("Cash on Delivery - Payment pending");

        return response;
    }
    public List<PaymentDto> getAllPayments() {
        return paymentRepository.findAll().stream()
                .map(payment -> {
                    PaymentDto dto = new PaymentDto();
                    dto.setPaymentId(payment.getId());
                    dto.setOrderId(payment.getOrderId());
                    dto.setPaymentMode(payment.getPaymentMode());
                    dto.setAmount(payment.getAmount());
                    dto.setStatus(payment.getStatus());
                    dto.setMessage(payment.getMessage());
                    return dto;
                })
                .toList();
    }



}
