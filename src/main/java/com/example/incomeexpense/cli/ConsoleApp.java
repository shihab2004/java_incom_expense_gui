package com.example.incomeexpense.cli;

import com.example.incomeexpense.db.LedgerEntryDao;
import com.example.incomeexpense.ml.SimpleLedgerValuePredictor;
import com.example.incomeexpense.model.EntryType;
import com.example.incomeexpense.model.LedgerEntry;
import com.example.incomeexpense.model.Totals;
import com.example.incomeexpense.util.MoneyFormat;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public final class ConsoleApp {

    private final Scanner in;
    private final LedgerEntryDao dao;

    public ConsoleApp() {
        this.in = new Scanner(System.in);
        this.dao = new LedgerEntryDao();
    }

    public void run() {
        printWelcome();

        while (true) {
            printMenu();
            String choice = readLine("Select option (1-5): ");
            if (choice == null) {
                return;
            }

            switch (choice) {
                case "1" -> addEntry(EntryType.INCOME);
                case "2" -> addEntry(EntryType.EXPENSE);
                case "3" -> showLedger();
                case "4" -> exportCsv();
                case "5" -> forecast();
                default -> System.out.println("Invalid option. Please choose 1-5.\n");
            }
        }
    }

    private void printWelcome() {
        System.out.println("====================================================");
        System.out.println("        INCOME / EXPENSE TRACKER  (CLI)");
        System.out.println("====================================================");
        System.out.println("Developed by: Orin, Ashraful, Shihab");
        System.out.println("Database   : data/app.db");
        System.out.println("Tip        : Press Ctrl+C to exit");
        System.out.println();
    }

    private void printMenu() {
        System.out.println("Options:");
        System.out.println("  1) Income");
        System.out.println("  2) Expense");
        System.out.println("  3) Show ledger");
        System.out.println("  4) Export CSV");
        System.out.println("  5) Forecast future ledger");
    }

    private void addEntry(EntryType type) {
        try {
            System.out.println();
            System.out.println(type == EntryType.INCOME ? "Add Income" : "Add Expense");

            LocalDate date = readDate("Date (YYYY-MM-DD) [today]: ");
            String category = readRequired("Category: ");
            String description = readLine("Description (optional): ");
            long amountCents = readAmountCents("Amount (e.g. 12.34): ");

            dao.insert(date, type, category, description, amountCents);
            System.out.println("Saved.\n");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
            System.out.println();
        }
    }

    private void showLedger() {
        System.out.println();
        List<LedgerEntry> entries = dao.listAll();
        Collections.reverse(entries);

        if (entries.isEmpty()) {
            System.out.println("Ledger is empty.\n");
            return;
        }

        System.out.printf("%-4s %-10s %-7s %-15s %-25s %12s\n", "ID", "DATE", "TYPE", "CATEGORY", "DESCRIPTION", "AMOUNT");
        System.out.println("-------------------------------------------------------------------------------");

        for (LedgerEntry entry : entries) {
            long signed = entry.type() == EntryType.INCOME ? entry.amountCents() : -entry.amountCents();
            String desc = entry.description() == null ? "" : entry.description();

            System.out.printf(
                    "%-4d %-10s %-7s %-15s %-25s %12s\n",
                    entry.id(),
                    entry.entryDate(),
                    entry.type(),
                    safeTrim(entry.category(), 8),
                    safeTrim(desc, 15),
                    MoneyFormat.centsToSignedDollars(signed)
            );
        }

        Totals totals = dao.totals();
        System.out.println("-------------------------------------------------------------------------------");
        System.out.println("Income : " + MoneyFormat.centsToDollars(totals.incomeCents()));
        System.out.println("Expense: " + MoneyFormat.centsToDollars(totals.expenseCents()));
        System.out.println("Balance: " + MoneyFormat.centsToSignedDollars(totals.balanceCents()));
        System.out.println();
    }

    private void exportCsv() {
        System.out.println();
        String pathText = "data/ledger.csv";
        Path out = Path.of(pathText);

        List<LedgerEntry> entries = dao.listAll();
        Collections.reverse(entries);

        CsvExporter.exportLedger(out, entries);
        System.out.println("Exported: " + out.toAbsolutePath().normalize());
        System.out.println();
    }

    private void forecast() {
        System.out.println();
        List<LedgerEntry> entries = dao.listAll();
        Collections.reverse(entries);

        if (entries.isEmpty()) {
            System.out.println("Not enough history to forecast (ledger is empty).\n");
            return;
        }

        int horizon = readInt("How many future steps? [5]: ", 5);
        if (horizon <= 0) {
            System.out.println("Horizon must be > 0.\n");
            return;
        }

        List<Long> history = new ArrayList<>(entries.size());
        for (LedgerEntry entry : entries) {
            long signed = entry.type() == EntryType.INCOME ? entry.amountCents() : -entry.amountCents();
            history.add(signed);
        }

        List<Long> predicted = SimpleLedgerValuePredictor.predictNextCents(history, horizon);
        System.out.println("Forecast (predicted net amounts, in dollars):");
        for (int i = 0; i < predicted.size(); i++) {
            System.out.printf("  %d) %s\n", i + 1, MoneyFormat.centsToSignedDollars(predicted.get(i)));
        }
        System.out.println();
    }

    private String readLine(String prompt) {
        System.out.print(prompt);
        return in.nextLine().trim();

    }

    private String readRequired(String prompt) {
        while (true) {
            String value = readLine(prompt);
            if (value != null && !value.trim().isEmpty()) {
                return value;
            }
            System.out.println("Required.");
        }
    }


    private LocalDate readDate(String prompt) {
        String value = readLine(prompt);
        if (value.isEmpty()) {
            return LocalDate.now();
        }
        try {
            return LocalDate.parse(value);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid date. Use YYYY-MM-DD");
        }
    }

    private long readAmountCents(String prompt) {
        while (true) {
            String value = readLine(prompt);
            try {
                return MoneyFormat.dollarsToCents(value);
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid amount: " + e.getMessage());
            }
        }
    }

    private int readInt(String prompt, int defaultValue) {
        String value = readLine(prompt);
        if (value == null) {
            return defaultValue;
        }
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(trimmed);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private static String safeTrim(String text, int maxLen) {
         if (text.length() > maxLen) {
             text = text.substring(0, maxLen) + "...";
         }
        return text;
    }
}
