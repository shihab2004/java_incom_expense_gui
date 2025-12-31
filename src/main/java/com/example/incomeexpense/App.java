package com.example.incomeexpense;

import com.example.incomeexpense.cli.ConsoleApp;
import com.example.incomeexpense.db.Database;

public final class App {
    public static void main(String[] args) {
        // Silence noisy ORMLite table/index INFO logs (slf4j-simple).
        // Keep warnings/errors so failures still show up.
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "warn");
        System.setProperty("org.slf4j.simpleLogger.log.com.j256.ormlite", "warn");

        Database.initialize();
        Runtime.getRuntime().addShutdownHook(new Thread(Database::closeQuietly));

        new ConsoleApp().run();
    }
}
