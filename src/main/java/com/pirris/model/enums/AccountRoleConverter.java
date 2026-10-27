package com.pirris.model.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Persists {@link AccountRole} through its raw value in a {@code role_raw} column.
 */
@Converter(autoApply = false)
public class AccountRoleConverter implements AttributeConverter<AccountRole, String> {

    @Override
    public String convertToDatabaseColumn(AccountRole attribute) {
        return (attribute == null ? AccountRole.USER : attribute).raw();
    }

    @Override
    public AccountRole convertToEntityAttribute(String dbData) {
        return AccountRole.fromRaw(dbData);
    }
}
