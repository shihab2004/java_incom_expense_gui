package com.example.incomeexpense.db;

import com.example.incomeexpense.model.EntryType;
import com.example.incomeexpense.model.LedgerEntry;
import com.example.incomeexpense.model.Totals;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

public final class LedgerEntryDao {

    public long insert(LocalDate entryDate, EntryType type, String category, String description, long amountCents) {
        String sql = "INSERT INTO ledger_entry(entry_date, type, category, description, amount_cents, created_at) VALUES (?,?,?,?,?,?)";
        try (Connection connection = Database.open();
             PreparedStatement ps = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, entryDate.toString());
            ps.setString(2, type.name());
            ps.setString(3, category);
            ps.setString(4, description);
            ps.setLong(5, amountCents);
            ps.setString(6, OffsetDateTime.now(ZoneOffset.UTC).toString());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
            }
            return -1;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert entry", e);
        }
    }

    public List<LedgerEntry> listAll() {
        String sql = "SELECT id, entry_date, type, category, description, amount_cents, created_at FROM ledger_entry ORDER BY entry_date DESC, id DESC";
        try (Connection connection = Database.open();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            List<LedgerEntry> result = new ArrayList<>();
            while (rs.next()) {
                result.add(map(rs));
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to list entries", e);
        }
    }

    public Totals totals() {
        String sql = "SELECT " +
                "COALESCE(SUM(CASE WHEN type='INCOME' THEN amount_cents END), 0) AS income_cents, " +
                "COALESCE(SUM(CASE WHEN type='EXPENSE' THEN amount_cents END), 0) AS expense_cents " +
                "FROM ledger_entry";

        try (Connection connection = Database.open();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return new Totals(rs.getLong("income_cents"), rs.getLong("expense_cents"));
            }
            return new Totals(0, 0);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to compute totals", e);
        }
    }

    private static LedgerEntry map(ResultSet rs) throws SQLException {
        return new LedgerEntry(
                rs.getLong("id"),
                LocalDate.parse(rs.getString("entry_date")),
                EntryType.valueOf(rs.getString("type")),
                rs.getString("category"),
                rs.getString("description"),
                rs.getLong("amount_cents"),
                OffsetDateTime.parse(rs.getString("created_at"))
        );
    }
}
