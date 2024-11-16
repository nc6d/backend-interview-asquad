package com.example.demo.service;

import com.example.demo.model.Balance;
import com.example.demo.repository.entity.BalanceEntity;

public interface BalanceService {

    Balance getOrCreate(String currency);

    void updateBalance(BalanceEntity balanceEntity);
}
