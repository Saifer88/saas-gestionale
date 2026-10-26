package com.pirris.model.enums;

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

    public String raw() {
        return raw;
    }

    public static InvoiceStatus fromRaw(String value) {
        return RawEnums.fromRaw(values(), InvoiceStatus::raw, value, DRAFT);
    }
}
