package com.example.incomeexpense.ui;

import com.example.incomeexpense.model.LedgerEntry;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public final class LedgerTableModel extends AbstractTableModel {
    private final String[] columns = new String[]{"Date", "Type", "Category", "Description", "Amount"};
    private final List<LedgerEntry> rows = new ArrayList<>();

    public void setRows(List<LedgerEntry> entries) {
        rows.clear();
        rows.addAll(entries);
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return rows.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public String getColumnName(int column) {
        return columns[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        LedgerEntry entry = rows.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> entry.entryDate().toString();
            case 1 -> entry.type().name();
            case 2 -> entry.category();
            case 3 -> entry.description() == null ? "" : entry.description();
            case 4 -> MoneyFormat.centsToDollars(entry.amountCents());
            default -> "";
        };
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }
}
