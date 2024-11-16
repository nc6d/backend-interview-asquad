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
    @Transactional(
            isolation = Isolation.REPEATABLE_READ,
            timeout = 5
    )
    public Balance getOrCreate(String currency) {
        try {
            log.debug("Attempting to get or create balance for currency: {}", currency);
            Optional<BalanceEntity> existingBalance = balanceRepository.findByCurrencyForUpdate(currency);

            if (existingBalance.isPresent()) {
                log.debug("Found existing balance for currency: {}", currency);
                return existingBalance.get();
            }

            log.debug("No existing balance found, creating new one for currency: {}", currency);
            return createBalanceWithRetry(currency);

        } catch (Exception e) {
            log.error("Error in getOrCreate for currency {}", currency, e);
            throw e;
        }
    }

    private BalanceEntity createBalanceWithRetry(String currency) {
        try {
            BalanceEntity newBalance = new BalanceEntity(currency);
            BalanceEntity saved = balanceRepository.saveAndFlush(newBalance);
            log.debug("Successfully created new balance for currency: {}", currency);
            return saved;

        } catch (DataIntegrityViolationException e) {
            log.debug("Concurrent creation detected for currency {}, retrying find", currency);
            entityManager.clear();

            return balanceRepository.findByCurrencyForUpdate(currency)
                    .orElseThrow(() -> {
                        log.error("Balance record not found after handling duplicate creation for currency: {}", currency);
                        return new IllegalStateException(
                                "Balance record not found after handling duplicate creation");
                    });
        }
    }

    @Override
    @Transactional
    public void updateBalance(BalanceEntity balanceEntity) {
        balanceRepository.save(balanceEntity);
    }
}
