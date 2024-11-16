package com.example.demo.exception;

public class DuplicateTransactionReferenceException extends RuntimeException {
    public DuplicateTransactionReferenceException(String reference) {
        super("Duplicate transaction reference: " + reference);
    }
}