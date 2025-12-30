package com.example.incomeexpense.ui;

import com.example.incomeexpense.db.LedgerEntryDao;
import com.example.incomeexpense.model.EntryType;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDate;

public final class EntryPanel extends JPanel {
    private final LedgerEntryDao dao;
    private final Runnable onSaved;

    private final JTextField dateField = new JTextField(12);
    private final JComboBox<EntryType> typeBox = new JComboBox<>(EntryType.values());
    private final JTextField categoryField = new JTextField(18);
    private final JTextField descriptionField = new JTextField(24);
    private final JTextField amountField = new JTextField(12);

    public EntryPanel(LedgerEntryDao dao, Runnable onSaved) {
        this.dao = dao;
        this.onSaved = onSaved;

        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        setLayout(new GridBagLayout());

        dateField.setText(LocalDate.now().toString());
        typeBox.setSelectedItem(EntryType.EXPENSE);

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 8, 8, 8);
        c.anchor = GridBagConstraints.WEST;

        c.gridx = 0; c.gridy = 0;
        add(new JLabel("Date (YYYY-MM-DD)"), c);
        c.gridx = 1;
        add(dateField, c);

        c.gridx = 0; c.gridy = 1;
        add(new JLabel("Type"), c);
        c.gridx = 1;
        add(typeBox, c);

        c.gridx = 0; c.gridy = 2;
        add(new JLabel("Category"), c);
        c.gridx = 1;
        add(categoryField, c);

        c.gridx = 0; c.gridy = 3;
        add(new JLabel("Description"), c);
        c.gridx = 1;
        add(descriptionField, c);

        c.gridx = 0; c.gridy = 4;
        add(new JLabel("Amount"), c);
        c.gridx = 1;
        add(amountField, c);

        JButton save = new JButton("Save Entry");
        save.addActionListener(e -> save());

        c.gridx = 1; c.gridy = 5;
        c.anchor = GridBagConstraints.EAST;
        add(save, c);
    }

    private void save() {
        try {
            LocalDate date = LocalDate.parse(dateField.getText().trim());
            EntryType type = (EntryType) typeBox.getSelectedItem();
            String category = categoryField.getText() == null ? "" : categoryField.getText().trim();
            String description = descriptionField.getText() == null ? "" : descriptionField.getText().trim();
            long amountCents = MoneyFormat.dollarsToCents(amountField.getText());

            if (type == null) {
                throw new IllegalArgumentException("Type required");
            }
            if (category.isEmpty()) {
                throw new IllegalArgumentException("Category required");
            }

            dao.insert(date, type, category, description.isEmpty() ? null : description, amountCents);
            JOptionPane.showMessageDialog(this, "Saved.");

            amountField.setText("");
            descriptionField.setText("");

            onSaved.run();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Cannot Save", JOptionPane.ERROR_MESSAGE);
        }
    }
}
