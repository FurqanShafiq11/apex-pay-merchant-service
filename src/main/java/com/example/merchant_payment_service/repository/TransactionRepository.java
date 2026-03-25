package com.example.merchant_payment_service.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.merchant_payment_service.model.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    Optional<Transaction> findByMerchantIdAndIdempotencyKey(String merchantId, String idempotencyKey);
}