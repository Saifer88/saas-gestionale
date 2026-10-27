package com.pirris.model.enums;

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

    public String raw() {
        return raw;
    }

    public static AccountRole fromRaw(String value) {
        return RawEnums.fromRaw(values(), AccountRole::raw, value, USER);
    }
}
