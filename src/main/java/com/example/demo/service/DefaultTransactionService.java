package com.example.demo.service;

import com.example.demo.exception.DuplicateTransactionReferenceException;
import com.example.demo.model.Balance;
import com.example.demo.model.Transaction;
import com.example.demo.model.TransactionStatus;
import com.example.demo.model.TransactionType;
import com.example.demo.repository.TransactionRepository;
import com.example.demo.repository.TransactionStatusRepository;
import com.example.demo.repository.entity.BalanceEntity;
import com.example.demo.repository.entity.TransactionEntity;
import com.example.demo.repository.entity.TransactionStatusEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class DefaultTransactionService implements TransactionService {

    private final BalanceService balanceService;
    private final TransactionRepository transactionRepository;
    private final TransactionStatusRepository transactionStatusRepository;

    @Override
    public Transaction create(TransactionType type, String reference, BigDecimal amount, String currency) {
        Balance balance = balanceService.getOrCreate(currency);

        if (type == TransactionType.WITHDRAWAL) {
            adjustBalance(balance, amount.negate());
        }

        TransactionEntity transaction = new TransactionEntity(
                balance.getId(),
                reference,
                type,
                amount,
                currency
        );

        try {
            transaction = transactionRepository.save(transaction);
            logStatusHistory(transaction.getId(), transaction.getStatus());

            // Simulate some processing logic
            processTransaction(transaction);
            return toSuccess(transaction.getId());

        } catch (DataIntegrityViolationException e) {
            log.error("Failed to create transaction due to constraint violation: {}", e.getMessage());
            transaction.setStatus(TransactionStatus.ERROR);
            saveTransactionAsRefError(transaction, e.getMessage());

            throw new DuplicateTransactionReferenceException(reference);

        } catch (Exception e) {
            log.error("Error processing transaction {}: {}", transaction.getId(), e.getMessage());
            return toError(transaction.getId());
        }
    }

    @Override
    public Transaction toSuccess(long id) {
        TransactionEntity transaction = getTransactionEntity(id);

        if (transaction.getStatus().isFinal()) {
            throw new IllegalStateException("Cannot update a final transaction: " + transaction.getStatus());
        }

        transaction.setStatus(transaction.getStatus().transitionTo(TransactionStatus.SUCCESS));
        transaction = transactionRepository.save(transaction);

        // Update balance for DEPOSIT transactions
        if (transaction.getType() == TransactionType.DEPOSIT) {
            Balance balance = balanceService.getOrCreate(transaction.getCurrency());
            adjustBalance(balance, transaction.getAmount());
        }

        logStatusHistory(transaction.getId(), transaction.getStatus());

        return transaction;
    }

    @Override
    public Transaction toError(long id) {
        TransactionEntity transaction = getTransactionEntity(id);

        if (transaction.getStatus().isFinal()) {
            throw new IllegalStateException("Cannot update a final transaction: " + transaction.getStatus());
        }

        transaction.setStatus(transaction.getStatus().transitionTo(TransactionStatus.ERROR));
        transaction = transactionRepository.save(transaction);

        // Rollback balance adjustment for WITHDRAWAL transactions
        if (transaction.getType() == TransactionType.WITHDRAWAL) {
            Balance balance = balanceService.getOrCreate(transaction.getCurrency());
            adjustBalance(balance, transaction.getAmount());
        }

        logStatusHistory(transaction.getId(), transaction.getStatus());

        return transaction;
    }

    @Override
    public Transaction toProcessing(long id) {
        TransactionEntity transaction = getTransactionEntity(id);

        if (transaction.getStatus().isFinal()) {
            throw new IllegalStateException("Cannot update a final transaction: " + transaction.getStatus());
        }

        if (transaction.getStatus() != TransactionStatus.NEW) {
            throw new IllegalStateException("Only NEW transactions can transition to PROCESSING");
        }

        transaction.setStatus(transaction.getStatus().transitionTo(TransactionStatus.PROCESSING));
        transaction = transactionRepository.save(transaction);

        logStatusHistory(transaction.getId(), TransactionStatus.PROCESSING);

        log.info("Transaction {} marked as PROCESSING", id);
        return transaction;
    }


    @Override
    public Optional<Transaction> find(long id) {
        return transactionRepository.findById(id).map(x -> x);
    }

    private void adjustBalance(Balance balance, BigDecimal amount) {
        BalanceEntity balanceEntity = (BalanceEntity) balance;
        balanceEntity.setAmount(balanceEntity.getAmount().add(amount));
        balanceService.updateBalance(balanceEntity);
    }

    private void logStatusHistory(long transactionId, TransactionStatus status) {
        TransactionStatusEntity statusEntity = new TransactionStatusEntity(transactionId, status);
        transactionStatusRepository.save(statusEntity);
    }

    private void processTransaction(TransactionEntity transaction) {

        transaction = (TransactionEntity) toProcessing(transaction.getId());

        if (transaction.getStatus() != TransactionStatus.PROCESSING) {
            throw new IllegalStateException("Transaction must be in PROCESSING state to proceed. Current state: " + transaction.getStatus());
        }

        if ("REF_FAIL".equals(transaction.getReference())) {
            throw new RuntimeException("Simulated processing failure");
        }
        log.info("Processing transaction {} successfully", transaction.getId());
    }

    // Additional logic to save record as error if reference duplication faced.
    // CAN BE REMOVED IF NOT REQUIRED
    private void saveTransactionAsRefError(TransactionEntity transaction, String errorMsg) {
        try {
            transaction.setStatus(TransactionStatus.ERROR);
            transaction.setReference(String.format("Duplicated reference: [%s]", transaction.getReference()));
            transaction = transactionRepository.save(transaction);

            // Rollback balance adjustment for WITHDRAWAL transactions
            if (transaction.getType() == TransactionType.WITHDRAWAL) {
                Balance balance = balanceService.getOrCreate(transaction.getCurrency());
                adjustBalance(balance, transaction.getAmount());
            }

            logStatusHistory(transaction.getId(), TransactionStatus.ERROR);

            log.info("Transaction {} marked as ERROR due to: {}", transaction.getId(), errorMsg);

        } catch (Exception ex) {
            log.error("Failed to save transaction with ERROR status: {}", ex.getMessage());
            throw new IllegalStateException("Failed to save transaction as ERROR", ex);
        }
    }


    private TransactionEntity getTransactionEntity(long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found: " + id));
    }
}
