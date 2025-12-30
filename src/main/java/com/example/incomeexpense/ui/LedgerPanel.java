package com.example.incomeexpense.ui;

import com.example.incomeexpense.db.LedgerEntryDao;
import com.example.incomeexpense.model.Totals;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import java.awt.BorderLayout;
import java.awt.FlowLayout;

public final class LedgerPanel extends JPanel {
    private final LedgerEntryDao dao;

    private final LedgerTableModel model = new LedgerTableModel();
    private final JLabel incomeLabel = new JLabel();
    private final JLabel expenseLabel = new JLabel();
    private final JLabel balanceLabel = new JLabel();

    public LedgerPanel(LedgerEntryDao dao) {
        this.dao = dao;

        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JTable table = new JTable(model);
        table.setFillsViewportHeight(true);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        top.add(new JLabel("Income:"));
        top.add(incomeLabel);
        top.add(new JLabel("Expense:"));
        top.add(expenseLabel);
        top.add(new JLabel("Balance:"));
        top.add(balanceLabel);

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> refresh());
        top.add(refreshButton);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        refresh();
    }

    public void refresh() {
        model.setRows(dao.listAll());

        Totals totals = dao.totals();
        incomeLabel.setText(MoneyFormat.centsToDollars(totals.incomeCents()));
        expenseLabel.setText(MoneyFormat.centsToDollars(totals.expenseCents()));
        balanceLabel.setText(MoneyFormat.centsToDollars(totals.balanceCents()));
    }
}
