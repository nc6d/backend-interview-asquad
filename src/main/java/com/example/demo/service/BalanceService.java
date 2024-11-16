package com.example.demo.service;

import com.example.demo.model.Balance;

public interface BalanceService {

    Balance getOrCreate(String currency);
}
