package com.pirris.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

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

    @JsonValue
    public String raw() {
        return raw;
    }

    public static ExpenseKind fromRaw(String value) {
        return RawEnums.fromRaw(values(), ExpenseKind::raw, value, ONE_TIME);
    }

    /** Strict JSON deserialization: unknown tokens are rejected (400). */
    @JsonCreator
    public static ExpenseKind fromJson(String value) {
        return RawEnums.parse(ExpenseKind.class, values(), ExpenseKind::raw, value);
    }
}
