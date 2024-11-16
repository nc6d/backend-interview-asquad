package com.example.demo.controller;

import com.example.demo.dto.TransactionCreateRequestDTO;
import com.example.demo.dto.TransactionCreateResponseDTO;
import com.example.demo.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/transaction")
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    public TransactionCreateResponseDTO create(@RequestBody final TransactionCreateRequestDTO dto) {
        var transaction = transactionService.create(
                dto.getType(),
                dto.getReference(),
                dto.getAmount(),
                dto.getCurrency()
        );

        return new TransactionCreateResponseDTO(transaction.getId());
    }
}
