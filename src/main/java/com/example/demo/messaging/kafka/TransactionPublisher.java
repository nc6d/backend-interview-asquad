package com.example.demo.messaging.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionPublisher {

    private final KafkaTemplate<String, TransactionEvent> kafkaTemplate;

    @EventListener
    public void handleTransactionEvent(TransactionEvent event) {
        log.info("Publishing transaction event to Kafka: {}", event);
        kafkaTemplate.send("transaction", event);
    }
}
