package com.example.merchant_payment_service.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.merchant_payment_service.dto.PaymentRequest;
import com.example.merchant_payment_service.model.Transaction;
import com.example.merchant_payment_service.model.TransactionStatus;
import com.example.merchant_payment_service.repository.TransactionRepository;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@Service
public class PaymentService {

    private final TransactionRepository repository;

    public PaymentService(TransactionRepository repository) {
        this.repository = repository;
    }

    @Transactional
    @CircuitBreaker(name = "bankService", fallbackMethod = "handleBankFailure")
    public Transaction processPayment(PaymentRequest request) {
        // 1. Idempotency Check
        Optional<Transaction> existingTx = repository.findByMerchantIdAndIdempotencyKey(
                request.getMerchantId(), request.getIdempotencyKey());

        if (existingTx.isPresent()) return existingTx.get();

        // 2. Create Pending Transaction
        Transaction transaction = new Transaction();
        transaction.setMerchantId(request.getMerchantId());
        transaction.setAmount(request.getAmount());
        transaction.setCurrency(request.getCurrency());
        transaction.setIdempotencyKey(request.getIdempotencyKey());
        transaction.setStatus(TransactionStatus.PENDING);
        transaction = repository.save(transaction);

        // 3. Simulate Bank Call (This is what the Circuit Breaker watches)
        boolean authSuccess = simulateBankAuthorization();

        if (authSuccess) {
            transaction.setStatus(TransactionStatus.SUCCESS);
        } else {
            transaction.setStatus(TransactionStatus.FAILED);
        }

        return repository.save(transaction);
    }

    // FALLBACK METHOD: This runs if the Bank is down or the Circuit is Open
    public Transaction handleBankFailure(PaymentRequest request, Throwable t) {
        Transaction transaction = new Transaction();
        transaction.setMerchantId(request.getMerchantId());
        transaction.setAmount(request.getAmount());
        transaction.setCurrency(request.getCurrency());
        transaction.setIdempotencyKey(request.getIdempotencyKey());
        transaction.setStatus(TransactionStatus.FAILED);
        // In a real bank, we'd log this specifically as a "System Timeout/Error"
        return transaction;
    }

    private boolean simulateBankAuthorization() {
        // Randomly simulate a slow/failing network (10% chance of error)
        if (Math.random() < 0.1) {
            throw new RuntimeException("Bank Network Timeout");
        }
        return true;
    }
}