package com.example.demo.model;

public enum TransactionStatus {

    NEW,
    PROCESSING,
    SUCCESS,
    ERROR,
    ;

    public boolean isFinal() {
        return this == SUCCESS || this == ERROR;
    }
}
