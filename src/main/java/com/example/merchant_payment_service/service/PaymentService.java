package com.example.merchant_payment_service.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.merchant_payment_service.dto.PaymentRequest;
import com.example.merchant_payment_service.model.Transaction;
import com.example.merchant_payment_service.model.TransactionStatus;
import com.example.merchant_payment_service.repository.TransactionRepository;

@Service
public class PaymentService {

    private final TransactionRepository repository;

    public PaymentService(TransactionRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Transaction processPayment(PaymentRequest request) {
        // 1. IDEMPOTENCY CHECK: Check if this transaction already exists
        Optional<Transaction> existingTx = repository.findByMerchantIdAndIdempotencyKey(
                request.getMerchantId(), request.getIdempotencyKey());

        if (existingTx.isPresent()) {
            // Return existing record instead of creating a new one
            return existingTx.get();
        }

        // 2. CREATE NEW TRANSACTION
        Transaction transaction = new Transaction();
        transaction.setMerchantId(request.getMerchantId());
        transaction.setAmount(request.getAmount());
        transaction.setCurrency(request.getCurrency());
        transaction.setIdempotencyKey(request.getIdempotencyKey());
        transaction.setStatus(TransactionStatus.PENDING);

        // Save initially as PENDING
        transaction = repository.save(transaction);

        // 3. SIMULATE EXTERNAL BANK AUTHORIZATION
        boolean authSuccess = simulateBankAuthorization();

        if (authSuccess) {
            transaction.setStatus(TransactionStatus.SUCCESS);
        } else {
            transaction.setStatus(TransactionStatus.FAILED);
        }

        // 4. UPDATE AND RETURN
        return repository.save(transaction);
    }

    private boolean simulateBankAuthorization() {
        // In a real bank, this would be an API call to a card network (Visa/MC)
        // For now, let's assume 90% of payments succeed
        return Math.random() < 0.9;
    }
}