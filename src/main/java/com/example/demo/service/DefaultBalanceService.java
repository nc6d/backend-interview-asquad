package com.example.demo.service;

import com.example.demo.model.Balance;
import com.example.demo.repository.BalanceRepository;
import com.example.demo.repository.entity.BalanceEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DefaultBalanceService implements BalanceService {

    private final BalanceRepository balanceRepository;

    @Override
    public Balance getOrCreate(String currency) {
        return balanceRepository.findByCurrency(currency)
                .orElseGet(() -> balanceRepository.save(new BalanceEntity(currency)));
    }
}
