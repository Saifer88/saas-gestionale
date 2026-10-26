package com.pirris.model.enums;

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

    public String raw() {
        return raw;
    }

    public static PackageKind fromRaw(String value) {
        return RawEnums.fromRaw(values(), PackageKind::raw, value, LESSONS);
    }
}
