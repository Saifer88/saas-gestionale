package com.pirris.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Lifecycle status of a {@code training_session}, persisted in
 * {@code status_raw}. Serialized in JSON as its raw token (e.g. {@code "noShow"}).
 */
public enum SessionStatus {
    /** Tentative booking: does not block overlaps. */
    PROVISIONAL("provisional"),
    PLANNED("planned"),
    COMPLETED("completed"),
    CANCELLED("cancelled"),
    NO_SHOW("noShow");

    private final String raw;

    SessionStatus(String raw) {
        this.raw = raw;
    }

    @JsonValue
    public String raw() {
        return raw;
    }

    public static SessionStatus fromRaw(String value) {
        return RawEnums.fromRaw(values(), SessionStatus::raw, value, PLANNED);
    }

    /** Strict JSON deserialization: unknown tokens are rejected (400). */
    @JsonCreator
    public static SessionStatus fromJson(String value) {
        return RawEnums.parse(SessionStatus.class, values(), SessionStatus::raw, value);
    }
}
