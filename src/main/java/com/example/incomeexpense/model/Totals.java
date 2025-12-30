package com.example.incomeexpense.model;

public record Totals(long incomeCents, long expenseCents) {
    public long balanceCents() {
        return incomeCents - expenseCents;
    }
}
