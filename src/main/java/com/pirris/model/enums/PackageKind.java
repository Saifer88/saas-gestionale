package com.pirris.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Kind of a {@code lesson_package}, persisted in {@code kind_raw}.
 * A {@code LESSONS} package tracks a credit count; a {@code TIME} package
 * tracks a validity window.
 */
public enum PackageKind {
    LESSONS("lessons"),
    TIME("time");

    private final String raw;

    PackageKind(String raw) {
        this.raw = raw;
    }

    @JsonValue
    public String raw() {
        return raw;
    }

    public static PackageKind fromRaw(String value) {
        return RawEnums.fromRaw(values(), PackageKind::raw, value, LESSONS);
    }

    /** Strict JSON deserialization: unknown tokens are rejected (400). */
    @JsonCreator
    public static PackageKind fromJson(String value) {
        return RawEnums.parse(PackageKind.class, values(), PackageKind::raw, value);
    }
}
