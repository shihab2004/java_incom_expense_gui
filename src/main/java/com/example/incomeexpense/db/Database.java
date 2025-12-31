package com.example.incomeexpense.db;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;

public final class Database {
    private static final String DB_FOLDER = "data";
    private static final String DB_FILE = "app.db";

    private static volatile ConnectionSource connectionSource;

    private Database() {
    }

    public static String jdbcUrl() {
        Path dbPath = Path.of(DB_FOLDER, DB_FILE).toAbsolutePath().normalize();
        return "jdbc:sqlite:" + dbPath;
    }

    public static void initialize() {

        connectionSource();

        try {
            TableUtils.createTableIfNotExists(connectionSource, LedgerEntryEntity.class);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize database schema", e);
        }
    }

    public static ConnectionSource connectionSource() {
        if (connectionSource != null) {
            return connectionSource;
        }
            try {
                connectionSource = new JdbcConnectionSource(jdbcUrl());
                return connectionSource;
            } catch (SQLException e) {
                throw new RuntimeException("Failed to open database", e);
            }
        // synchronized (Database.class) {
        //     if (connectionSource != null) {
        //         return connectionSource;
        //     }

        // }
    }

    public static void close() {

        if (connectionSource != null) {
            try {
                connectionSource.close();
                connectionSource = null;
            } catch (Exception e) {
                throw new RuntimeException("Failed to close database", e);
            }
        }
    }


}
