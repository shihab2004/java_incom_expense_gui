package com.example.incomeexpense.ui;

import com.example.incomeexpense.db.LedgerEntryDao;
import com.example.incomeexpense.ml.SimpleLedgerValuePredictor;
import com.example.incomeexpense.model.EntryType;
import com.example.incomeexpense.model.LedgerEntry;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class MlIntegrationPanel extends JPanel {
    private final LedgerEntryDao dao;

    private final MlPredictionTableModel model = new MlPredictionTableModel();
    private final JComboBox<Integer> horizonCombo = new JComboBox<>(new Integer[]{10, 20});
    private final JComboBox<String> targetCombo = new JComboBox<>(new String[]{"Income", "Expense"});

    public MlIntegrationPanel(LedgerEntryDao dao) {
        this.dao = dao;

        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        top.add(new JLabel("Predict next:"));
        top.add(horizonCombo);

        top.add(new JLabel("For:"));
        top.add(targetCombo);

        JButton predictButton = new JButton("Predict");
        predictButton.addActionListener(e -> predict());
        top.add(predictButton);

        JTable table = new JTable(model);
        table.setFillsViewportHeight(true);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void predict() {
        Integer horizon = (Integer) horizonCombo.getSelectedItem();
        int steps = horizon == null ? 10 : horizon;

        String target = (String) targetCombo.getSelectedItem();
        if (target == null) {
            target = "Income";
        }

        List<LedgerEntry> entries = dao.listAll();
        if (entries.isEmpty()) {
            model.setPredictions(List.of());
            JOptionPane.showMessageDialog(this, "No ledger entries yet.", "ML Integration", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // dao.listAll() returns newest-first; convert to chronological for time-series prediction.
        List<LedgerEntry> chronological = new ArrayList<>(entries);
        Collections.reverse(chronological);

        if ("Income".equals(target)) {
            List<Long> incomeCents = new ArrayList<>();
            for (LedgerEntry entry : chronological) {
                if (entry.type() == EntryType.INCOME) {
                    incomeCents.add(entry.amountCents());
                }
            }
            if (incomeCents.isEmpty()) {
                model.setPredictions(List.of(), "INCOME");
                JOptionPane.showMessageDialog(this, "No INCOME entries to learn from yet.", "ML Integration", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            List<Long> predicted = SimpleLedgerValuePredictor.predictNextCents(incomeCents, steps);
            model.setPredictions(predicted, "INCOME");
            return;
        }

        if ("Expense".equals(target)) {
            List<Long> expenseCents = new ArrayList<>();
            for (LedgerEntry entry : chronological) {
                if (entry.type() == EntryType.EXPENSE) {
                    expenseCents.add(entry.amountCents());
                }
            }
            if (expenseCents.isEmpty()) {
                model.setPredictions(List.of(), "EXPENSE");
                JOptionPane.showMessageDialog(this, "No EXPENSE entries to learn from yet.", "ML Integration", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            List<Long> predicted = SimpleLedgerValuePredictor.predictNextCents(expenseCents, steps);
            model.setPredictions(predicted, "EXPENSE");
            return;
        }
    }
}
