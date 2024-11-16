package com.example.demo.model;

public enum TransactionStatus {

    NEW {
        @Override
        public boolean canTransitionTo(TransactionStatus nextStatus) {
            return nextStatus == PROCESSING;
        }

        @Override
        public boolean isFinal() {
            return false;
        }
    },
    PROCESSING {
        @Override
        public boolean canTransitionTo(TransactionStatus nextStatus) {
            return nextStatus == SUCCESS || nextStatus == ERROR;
        }

        @Override
        public boolean isFinal() {
            return false;
        }
    },
    SUCCESS {
        @Override
        public boolean canTransitionTo(TransactionStatus nextStatus) {
            return false; // Terminal state
        }

        @Override
        public boolean isFinal() {
            return true;
        }
    },
    ERROR {
        @Override
        public boolean canTransitionTo(TransactionStatus nextStatus) {
            return false; // Terminal state
        }

        @Override
        public boolean isFinal() {
            return true;
        }
    };

    public abstract boolean canTransitionTo(TransactionStatus nextStatus);

    public abstract boolean isFinal();

    public TransactionStatus transitionTo(TransactionStatus nextStatus) {
        if (!canTransitionTo(nextStatus)) {
            throw new IllegalStateException(
                    String.format("Invalid transition from %s to %s", this.name(), nextStatus.name())
            );
        }
        return nextStatus;
    }
}
