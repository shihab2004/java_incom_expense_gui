package com.example.incomeexpense.ui;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public final class MlPredictionTableModel extends AbstractTableModel {
    private final String[] columns = new String[]{"Step", "Type", "Amount"};
    private final List<Long> predictedCents = new ArrayList<>();
    private String fixedType = null;

    public void setPredictions(List<Long> cents) {
        setPredictions(cents, null);
    }

    public void setPredictions(List<Long> cents, String fixedType) {
        predictedCents.clear();
        if (cents != null) {
            predictedCents.addAll(cents);
        }
        this.fixedType = fixedType;
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return predictedCents.size();
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
        long cents = predictedCents.get(rowIndex);
        String type = fixedType != null ? fixedType : (cents < 0 ? "EXPENSE" : "INCOME");
        long absCents = Math.abs(cents);

        return switch (columnIndex) {
            case 0 -> String.valueOf(rowIndex + 1);
            case 1 -> type;
            case 2 -> MoneyFormat.centsToDollars(absCents);
            default -> "";
        };
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }
}
