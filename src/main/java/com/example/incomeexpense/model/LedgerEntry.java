package com.example.incomeexpense.model;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public record LedgerEntry(
        long id,
        LocalDate entryDate,
        EntryType type,
        String category,
        String description,
        long amountCents,
        OffsetDateTime createdAt
) {
}
