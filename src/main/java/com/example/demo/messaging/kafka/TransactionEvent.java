package com.example.demo.messaging.kafka;

import com.example.demo.model.TransactionStatus;
import com.example.demo.model.TransactionType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TransactionEvent {
    private long transactionId;
    private String reference;
    private TransactionType type;
    private TransactionStatus status;
    private BigDecimal amount;
    private String currency;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime timestamp;

}
