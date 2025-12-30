package com.example.incomeexpense;

import com.example.incomeexpense.db.Database;
import com.example.incomeexpense.ui.MainFrame;

import javax.swing.SwingUtilities;

public final class App {
    public static void main(String[] args) {
        Database.initialize();

        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
