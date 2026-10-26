package com.pirris.model;

import java.time.LocalDateTime;

import com.pirris.model.enums.ExpenseKind;
import com.pirris.model.enums.ExpenseKindConverter;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * A one-time or recurring business/personal expense (table {@code expense}).
 */
@Entity
@Table(name = "expense")
public class ExpenseModel extends AuditedModel {

    @NotBlank
    @Column(name = "name", nullable = false)
    public String name;

    @NotNull
    @Column(name = "date", nullable = false)
    public LocalDateTime date;

    @Column(name = "amount_cents", nullable = false)
    public long amountCents = 0;

    @Convert(converter = ExpenseKindConverter.class)
    @Column(name = "kind_raw", nullable = false)
    public ExpenseKind kind = ExpenseKind.ONE_TIME;

    @Column(name = "source_key", nullable = false)
    public String sourceKey = "";

    @Column(name = "is_personal", nullable = false)
    public boolean personal = false;
}
