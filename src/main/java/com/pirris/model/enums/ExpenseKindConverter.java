package com.pirris.model.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Persists {@link ExpenseKind} through its raw value in a {@code kind_raw} column.
 */
@Converter(autoApply = false)
public class ExpenseKindConverter implements AttributeConverter<ExpenseKind, String> {

    @Override
    public String convertToDatabaseColumn(ExpenseKind attribute) {
        return (attribute == null ? ExpenseKind.ONE_TIME : attribute).raw();
    }

    @Override
    public ExpenseKind convertToEntityAttribute(String dbData) {
        return ExpenseKind.fromRaw(dbData);
    }
}
