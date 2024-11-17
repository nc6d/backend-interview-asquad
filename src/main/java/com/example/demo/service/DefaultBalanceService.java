package com.example.demo.service;

import com.example.demo.model.Balance;
import com.example.demo.repository.BalanceRepository;
import com.example.demo.repository.entity.BalanceEntity;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class DefaultBalanceService implements BalanceService {
    private final BalanceRepository balanceRepository;
    private final EntityManager entityManager;

    @Override
    @Transactional
    public Balance getOrCreate(String currency) {
        // Attempt to insert a new balance
        Long balanceId = balanceRepository.insertIfNotExists(currency);

        if (balanceId != null) {
            // A new balance was successfully created
            return balanceRepository.findById(balanceId)
                    .orElseThrow(() -> new IllegalStateException("Failed to retrieve newly created balance"));
        }

        // The balance already exists, retrieve it
        return balanceRepository.findByCurrency(currency)
                .orElseThrow(() -> new IllegalStateException("Balance record not found for currency: " + currency));
    }

    @Override
    @Transactional
    public void updateBalance(BalanceEntity balanceEntity) {
        balanceRepository.save(balanceEntity);
    }
}
