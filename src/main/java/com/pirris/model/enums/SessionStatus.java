package com.pirris.model.enums;

/**
 * Lifecycle status of a {@code training_session}, persisted in
 * {@code status_raw}.
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

    public String raw() {
        return raw;
    }

    public static SessionStatus fromRaw(String value) {
        return RawEnums.fromRaw(values(), SessionStatus::raw, value, PLANNED);
    }
}
