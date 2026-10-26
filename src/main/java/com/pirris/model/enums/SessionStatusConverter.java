package com.pirris.model.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Persists {@link SessionStatus} through its raw value (e.g. {@code "planned"},
 * {@code "noShow"}) in the {@code status_raw} column. A converter is used rather
 * than {@code @Enumerated(STRING)} because the stored tokens are camelCase and
 * do not match the Java constant names.
 */
@Converter(autoApply = false)
public class SessionStatusConverter implements AttributeConverter<SessionStatus, String> {

    @Override
    public String convertToDatabaseColumn(SessionStatus attribute) {
        return (attribute == null ? SessionStatus.PLANNED : attribute).raw();
    }

    @Override
    public SessionStatus convertToEntityAttribute(String dbData) {
        return SessionStatus.fromRaw(dbData);
    }
}
