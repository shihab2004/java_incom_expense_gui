package com.example.incomeexpense.db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "ledger_entry")
public final class LedgerEntryEntity {

    @DatabaseField(generatedId = true)
    private long id;

    @DatabaseField(columnName = "entry_date", canBeNull = false, index = true)
    private String entryDate;

    @DatabaseField(canBeNull = false, index = true)
    private String type;

    @DatabaseField(canBeNull = false)
    private String category;

    @DatabaseField
    private String description;

    @DatabaseField(columnName = "amount_cents", canBeNull = false)
    private long amountCents;

    @DatabaseField(columnName = "created_at", canBeNull = false)
    private String createdAt;

    public LedgerEntryEntity() {
        // ORMLite requires a no-arg constructor.
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(String entryDate) {
        this.entryDate = entryDate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getAmountCents() {
        return amountCents;
    }

    public void setAmountCents(long amountCents) {
        this.amountCents = amountCents;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
