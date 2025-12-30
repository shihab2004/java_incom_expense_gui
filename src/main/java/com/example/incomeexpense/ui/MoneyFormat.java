package com.example.incomeexpense.ui;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class MoneyFormat {
    private MoneyFormat() {
    }

    public static long dollarsToCents(String text) {
        if (text == null) {
            throw new IllegalArgumentException("Amount required");
        }
        String trimmed = text.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("Amount required");
        }

        BigDecimal value;
        try {
            value = new BigDecimal(trimmed);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid amount");
        }

        if (value.signum() < 0) {
            throw new IllegalArgumentException("Amount must be >= 0");
        }

        BigDecimal cents = value.multiply(BigDecimal.valueOf(100)).setScale(0, RoundingMode.HALF_UP);
        return cents.longValueExact();
    }

    public static String centsToDollars(long cents) {
        BigDecimal value = BigDecimal.valueOf(cents).divide(BigDecimal.valueOf(100), 2, RoundingMode.UNNECESSARY);
        return value.toPlainString();
    }
}
