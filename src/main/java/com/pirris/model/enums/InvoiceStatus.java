package com.pirris.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Status of an {@code invoice}, persisted in {@code status_raw}.
 */
public enum InvoiceStatus {
    DRAFT("draft"),
    ISSUED("issued"),
    PAID("paid"),
    CANCELLED("cancelled");

    private final String raw;

    InvoiceStatus(String raw) {
        this.raw = raw;
    }

    @JsonValue
    public String raw() {
        return raw;
    }

    public static InvoiceStatus fromRaw(String value) {
        return RawEnums.fromRaw(values(), InvoiceStatus::raw, value, DRAFT);
    }

    /** Strict JSON deserialization: unknown tokens are rejected (400). */
    @JsonCreator
    public static InvoiceStatus fromJson(String value) {
        return RawEnums.parse(InvoiceStatus.class, values(), InvoiceStatus::raw, value);
    }
}
