package com.example.incomeexpense.cli;

import com.example.incomeexpense.model.LedgerEntry;
import com.example.incomeexpense.model.EntryType;
import com.example.incomeexpense.util.MoneyFormat;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public final class CsvExporter {
    private CsvExporter() {
    }

    public static void exportLedger(Path outputFile, List<LedgerEntry> entries) {
        try {
            Path parent = outputFile.toAbsolutePath().normalize().getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }

            try (BufferedWriter writer = Files.newBufferedWriter(outputFile, StandardCharsets.UTF_8)) {
                writer.write("id,entry_date,type,category,description,amount_dollars,amount_cents,created_at");
                writer.newLine();

                for (LedgerEntry entry : entries) {
                    writer.write("");
                    writer.write(csv(entry.id()));
                    writer.write(',');
                    writer.write(csv(entry.entryDate().toString()));
                    writer.write(',');
                    writer.write(csv(entry.type().name()));
                    writer.write(',');
                    writer.write(csv(entry.category()));
                    writer.write(',');
                    writer.write(csv(entry.description()));
                    writer.write(',');
                    writer.write(csv(MoneyFormat.centsToSignedDollars(signedAmount(entry))));
                    writer.write(',');
                    writer.write(csv(Long.toString(signedAmount(entry))));
                    writer.write(',');
                    writer.write(csv(entry.createdAt().toString()));
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to export CSV: " + outputFile, e);
        }
    }

    private static long signedAmount(LedgerEntry entry) {
        return entry.type() == EntryType.INCOME ? entry.amountCents() : -entry.amountCents();
    }

    private static String csv(Object value) {
        if (value == null) {
            return "";
        }
        String s = String.valueOf(value);
        return s;
    }
}
