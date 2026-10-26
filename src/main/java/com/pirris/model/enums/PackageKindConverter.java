package com.pirris.model.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Persists {@link PackageKind} through its raw value in a {@code kind_raw} column.
 */
@Converter(autoApply = false)
public class PackageKindConverter implements AttributeConverter<PackageKind, String> {

    @Override
    public String convertToDatabaseColumn(PackageKind attribute) {
        return (attribute == null ? PackageKind.LESSONS : attribute).raw();
    }

    @Override
    public PackageKind convertToEntityAttribute(String dbData) {
        return PackageKind.fromRaw(dbData);
    }
}
