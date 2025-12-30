package com.example.incomeexpense.db;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public final class Database {
    private static final String DB_FOLDER = "data";
    private static final String DB_FILE = "app.db";

    private Database() {
    }

    public static String jdbcUrl() {
        Path dbPath = Path.of(DB_FOLDER, DB_FILE).toAbsolutePath().normalize();
        return "jdbc:sqlite:" + dbPath;
    }

    public static void initialize() {
        try {
            Files.createDirectories(Path.of(DB_FOLDER));
        } catch (Exception e) {
            throw new RuntimeException("Failed to create data directory", e);
        }

        try (Connection connection = open(); Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA foreign_keys = ON");
            statement.execute(
                    "CREATE TABLE IF NOT EXISTS ledger_entry (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "entry_date TEXT NOT NULL," +
                            "type TEXT NOT NULL CHECK(type IN ('INCOME','EXPENSE'))," +
                            "category TEXT NOT NULL," +
                            "description TEXT," +
                            "amount_cents INTEGER NOT NULL CHECK(amount_cents >= 0)," +
                            "created_at TEXT NOT NULL" +
                            ")"
            );

            statement.execute("CREATE INDEX IF NOT EXISTS idx_ledger_entry_date ON ledger_entry(entry_date)");
            statement.execute("CREATE INDEX IF NOT EXISTS idx_ledger_entry_type ON ledger_entry(type)");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize database", e);
        }
    }

    public static Connection open() throws SQLException {
        return DriverManager.getConnection(jdbcUrl());
    }
}
