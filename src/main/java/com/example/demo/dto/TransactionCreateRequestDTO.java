package com.example.demo.dto;

import com.example.demo.model.TransactionType;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TransactionCreateRequestDTO {

    @NotNull(message = "Transaction type is required")
    @JsonProperty(value = "type")
    private TransactionType type;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    @JsonProperty(value = "amount")
    private BigDecimal amount;

    @NotBlank(message = "Currency is required")
    @Size(min = 3, max = 3, message = "Currency must be represented as 3-letter code")
    @JsonProperty(value = "currency")
    private String currency;

    @NotBlank(message = "Reference is required")
    @JsonProperty(value = "reference")
    private String reference;
}
