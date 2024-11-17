package com.example.demo.messaging.kafka;

import com.example.demo.repository.TransactionDocumentRepository;
import com.example.demo.repository.document.TransactionDocument;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionListener {

	private final TransactionDocumentRepository transactionDocumentRepository;

	@KafkaListener(topics = "transaction", containerFactory = "kafkaListenerContainerFactory")
	public void listen(TransactionEvent event) {
		log.info("Consumed transaction event from Kafka: {}", event);

		TransactionDocument transactionDocument = new TransactionDocument(
				event.getTransactionId(),
				event.getReference(),
				event.getType(),
				event.getStatus(),
				event.getAmount(),
				event.getCurrency(),
				event.getTimestamp(),
				LocalDateTime.now()
		);
		transactionDocumentRepository.save(transactionDocument);

		log.info("Transaction event saved to MongoDB: {}", transactionDocument);
	}
}
