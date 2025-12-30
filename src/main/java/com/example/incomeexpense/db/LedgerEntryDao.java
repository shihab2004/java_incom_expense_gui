package com.example.incomeexpense.db;

import com.example.incomeexpense.model.EntryType;
import com.example.incomeexpense.model.LedgerEntry;
import com.example.incomeexpense.model.Totals;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

public final class LedgerEntryDao {

    private final Dao<LedgerEntryEntity, Long> dao;

    public LedgerEntryDao() {
        try {
            this.dao = DaoManager.createDao(Database.connectionSource(), LedgerEntryEntity.class);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize LedgerEntryDao", e);
        }
    }

    public long insert(LocalDate entryDate, EntryType type, String category, String description, long amountCents) {
        LedgerEntryEntity entity = new LedgerEntryEntity();
        entity.setEntryDate(entryDate.toString());
        entity.setType(type.name());
        entity.setCategory(category);
        entity.setDescription(description);
        entity.setAmountCents(amountCents);
        entity.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC).toString());

        try {
            dao.create(entity);
            return entity.getId();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert entry", e);
        }
    }

    public List<LedgerEntry> listAll() {
        try {
            QueryBuilder<LedgerEntryEntity, Long> qb = dao.queryBuilder();
            qb.orderBy("entry_date", false);
            qb.orderBy("id", false);

            List<LedgerEntryEntity> entities = dao.query(qb.prepare());
            List<LedgerEntry> result = new ArrayList<>(entities.size());
            for (LedgerEntryEntity entity : entities) {
                result.add(map(entity));
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to list entries", e);
        }
    }

    public Totals totals() {
        try {
            long income = 0;
            long expense = 0;

            for (LedgerEntryEntity entity : dao.queryForAll()) {
                EntryType type = EntryType.valueOf(entity.getType());
                if (type == EntryType.INCOME) {
                    income += entity.getAmountCents();
                } else {
                    expense += entity.getAmountCents();
                }
            }
            return new Totals(income, expense);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to compute totals", e);
        }
    }

    private static LedgerEntry map(LedgerEntryEntity entity) {
        return new LedgerEntry(
                entity.getId(),
                LocalDate.parse(entity.getEntryDate()),
                EntryType.valueOf(entity.getType()),
                entity.getCategory(),
                entity.getDescription(),
                entity.getAmountCents(),
                OffsetDateTime.parse(entity.getCreatedAt())
        );
    }
}
