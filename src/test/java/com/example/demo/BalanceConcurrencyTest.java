package com.example.demo;

import com.example.demo.model.Transaction;
import com.example.demo.model.TransactionType;
import com.example.demo.repository.BalanceRepository;
import com.example.demo.repository.TransactionRepository;
import com.example.demo.repository.entity.BalanceEntity;
import com.example.demo.repository.entity.TransactionEntity;
import com.example.demo.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class BalanceConcurrencyTest {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private BalanceRepository balanceRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Test
    @Timeout(10)
    void testNoDuplicateBalanceRecords() throws InterruptedException {
        String currency = "USD";

        int threadCount = 10;

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        List<Future<Transaction>> results = new ArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            results.add(executor.submit(() -> {
                latch.countDown(); // Ensure all threads start at the same time
                latch.await();
                return transactionService.create(
                        TransactionType.DEPOSIT,
                        "REF" + Thread.currentThread().getId(),
                        new BigDecimal("100.00"),
                        currency
                );
            }));
        }

        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);

        // Verify that only one balance record exists for the given currency
        List<BalanceEntity> balances = balanceRepository.findAll();
        assertEquals(1, balances.size(), "Only one balance should exist for currency: " + currency);
        BalanceEntity balance = balances.get(0);
        assertEquals(currency, balance.getCurrency());

        // Verify that all transactions reference the same balance
        results.forEach(result -> {
            try {
                Transaction transaction = result.get();
                TransactionEntity transactionEntity = transactionRepository.findById(transaction.getId())
                        .orElseThrow(() -> new AssertionError("Transaction not found"));
                assertEquals(balance.getId(), transactionEntity.getBalanceId());
            } catch (Exception e) {
                throw new RuntimeException("Unexpected exception while retrieving results", e);
            }
        });
    }
}
