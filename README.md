# Prepare

## Install

```bash
./mvnw clean compile
```

## Run docker

```bash
docker compose up -d
```

# Tasks

## Task

- Find a solution to implement in different way method isFinal in enum `TransactionStatus`.

## Task

- Implement validation of request DTO in `TransactionController` using `@Validated` annotation.
- Response with 400 Bad Request if validation fails.
    - Add details of validation errors to response.

## Task

- Solve problem of duplicated `reference` column. We have to respond with 409 Conflict if the same reference is used.

## Task

- Implement update status of transaction.
- Write status for history on create and update to `transaction_status` table. We have to understand the status of the
  transaction at any point in time.
- Do not update the status if it is the same.
- Do not allow to update status if current is final.
- Do not forget update `status` in `transaction` table.
- Use transactional approach. If any error occurs, rollback all changes.

## Task

- On transaction type WITHDRAWAL
    - substract amount from balance on create
    - on success do nothing
    - on error return amount to balance
- On transaction type DEPOSIT
    - add amount on balance on success
- Use transactional approach. If any error occurs, rollback all changes.

## Task

- Solve problem of duplicated balance record if two transactions are created at the same time with the same currency.

## Task

- Notify to kafka topic `transaction` on create and update transaction.
- Try to use `org.springframework.context.ApplicationEventPublisher` to publish events not in `TransactionService`.

## Task

- Implement kafka listener to consume messages from topic `transaction`.
- Write consumed messages to MongoDB collection `transaction`.
    - Use `com.example.demo.repository.TransactionDocumentRepository`

## Task

- Check all TODOs in the project make sure that all are implemented or remove it.