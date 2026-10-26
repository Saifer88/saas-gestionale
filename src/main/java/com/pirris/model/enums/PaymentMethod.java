package com.pirris.model.enums;

/**
 * Payment method shared by packages, participants and ledger entries, persisted
 * in {@code payment_method_raw}.
 */
public enum PaymentMethod {
    CASH("cash"),
    CARD("card"),
    TRANSFER("transfer"),
    OTHER("other");

    private final String raw;

    PaymentMethod(String raw) {
        this.raw = raw;
    }

    public String raw() {
        return raw;
    }

    public static PaymentMethod fromRaw(String value) {
        return RawEnums.fromRaw(values(), PaymentMethod::raw, value, CASH);
    }
}
