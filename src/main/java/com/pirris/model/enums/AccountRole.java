package com.pirris.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Authorization role of an {@code account}.
 *
 * <ul>
 *   <li>{@code ADMIN}: can register new studios and accounts.</li>
 *   <li>{@code USER}: trainer/operator; can log in and change own password.</li>
 * </ul>
 */
public enum AccountRole {
    ADMIN("admin"),
    USER("user");

    private final String raw;

    AccountRole(String raw) {
        this.raw = raw;
    }

    @JsonValue
    public String raw() {
        return raw;
    }

    public static AccountRole fromRaw(String value) {
        return RawEnums.fromRaw(values(), AccountRole::raw, value, USER);
    }

    /** Strict JSON deserialization: unknown tokens are rejected (400). */
    @JsonCreator
    public static AccountRole fromJson(String value) {
        return RawEnums.parse(AccountRole.class, values(), AccountRole::raw, value);
    }
}
