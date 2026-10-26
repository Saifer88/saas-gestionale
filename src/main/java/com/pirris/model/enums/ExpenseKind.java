package com.pirris.model.enums;

/**
 * Kind of an {@code expense}, persisted in {@code kind_raw}.
 */
public enum ExpenseKind {
    ONE_TIME("oneTime"),
    RECURRING("recurring");

    private final String raw;

    ExpenseKind(String raw) {
        this.raw = raw;
    }

    public String raw() {
        return raw;
    }

    public static ExpenseKind fromRaw(String value) {
        return RawEnums.fromRaw(values(), ExpenseKind::raw, value, ONE_TIME);
    }
}
