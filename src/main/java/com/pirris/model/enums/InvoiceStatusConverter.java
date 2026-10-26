package com.pirris.model.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Persists {@link InvoiceStatus} through its raw value in a {@code status_raw} column.
 */
@Converter(autoApply = false)
public class InvoiceStatusConverter implements AttributeConverter<InvoiceStatus, String> {

    @Override
    public String convertToDatabaseColumn(InvoiceStatus attribute) {
        return (attribute == null ? InvoiceStatus.DRAFT : attribute).raw();
    }

    @Override
    public InvoiceStatus convertToEntityAttribute(String dbData) {
        return InvoiceStatus.fromRaw(dbData);
    }
}
