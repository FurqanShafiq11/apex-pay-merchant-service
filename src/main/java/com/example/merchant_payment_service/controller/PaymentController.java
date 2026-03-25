package com.example.merchant_payment_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.merchant_payment_service.dto.PaymentRequest;
import com.example.merchant_payment_service.model.Transaction;
import com.example.merchant_payment_service.service.PaymentService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<Transaction> createPayment(@Valid @RequestBody PaymentRequest request) {
        Transaction transaction = paymentService.processPayment(request);
        
        // If the transaction was created successfully, return 201 Created
        // If it was already successful (Idempotent hit), return 200 OK
        return new ResponseEntity<>(transaction, HttpStatus.CREATED);
    }
}