package com.example.demo.messaging.kafka;

import com.example.demo.repository.entity.TransactionEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    public void publishTransactionEvent(TransactionEntity transaction) {
        TransactionEvent event = new TransactionEvent(
                transaction.getId(),
                transaction.getReference(),
                transaction.getType(),
                transaction.getStatus(),
                transaction.getAmount(),
                transaction.getCurrency(),
                transaction.getUpdatedAt()
        );
        log.info("Publishing transaction event for ID: {}", transaction.getId());
        eventPublisher.publishEvent(event);
    }
}
