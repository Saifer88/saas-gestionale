package com.pirris.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Payment method shared by packages and session participants, persisted in
 * {@code payment_method_raw}. Serialized in JSON as its raw token.
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

    @JsonValue
    public String raw() {
        return raw;
    }

    public static PaymentMethod fromRaw(String value) {
        return RawEnums.fromRaw(values(), PaymentMethod::raw, value, CASH);
    }

    /** Strict JSON deserialization: unknown tokens are rejected (400). */
    @JsonCreator
    public static PaymentMethod fromJson(String value) {
        return RawEnums.parse(PaymentMethod.class, values(), PaymentMethod::raw, value);
    }
}
