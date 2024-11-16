package com.example.demo.service;

import com.example.demo.model.Balance;
import com.example.demo.model.Transaction;
import com.example.demo.model.TransactionType;
import com.example.demo.repository.TransactionRepository;
import com.example.demo.repository.entity.TransactionEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class DefaultTransactionService implements TransactionService {

    private final BalanceService balanceService;
    private final TransactionRepository transactionRepository;

    @Override
    public Transaction create(
            final TransactionType type,
            final String reference,
            final BigDecimal amount,
            final String currency
    ) {
        Balance balance = balanceService.getOrCreate(currency);

        return transactionRepository.save(new TransactionEntity(
                balance.getId(),
                reference,
                type,
                amount,
                currency
        ));
    }

    @Override
    public Transaction toSuccess(long id) {
        // TODO implement
        return null;
    }

    @Override
    public Transaction toError(long id) {
        // TODO implement
        return null;
    }

    @Override
    public Optional<Transaction> find(long id) {
        return transactionRepository.findById(id).map(x -> x);
    }
}
