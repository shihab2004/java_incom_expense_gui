package com.example.incomeexpense.ui;

import com.example.incomeexpense.db.LedgerEntryDao;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;

public final class MainFrame extends JFrame {
    private final LedgerEntryDao dao = new LedgerEntryDao();

    public MainFrame() {
        super("Income / Expense Tracker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        LedgerPanel ledgerPanel = new LedgerPanel(dao);
        EntryPanel entryPanel = new EntryPanel(dao, ledgerPanel::refresh);
        MlIntegrationPanel mlIntegrationPanel = new MlIntegrationPanel(dao);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Add Entry", entryPanel);
        tabs.addTab("Ledger & Balance", ledgerPanel);
        tabs.addTab("ML Integration", mlIntegrationPanel);

        setLayout(new BorderLayout());
        add(tabs, BorderLayout.CENTER);
    }
}
