package com.pirris.model;

import java.time.LocalDateTime;
import java.util.UUID;

import com.pirris.model.enums.InvoiceStatus;
import com.pirris.model.enums.InvoiceStatusConverter;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * A billing document issued to a client (table {@code invoice}).
 */
@Entity
@Table(name = "invoice")
public class InvoiceModel extends AuditedModel {

    @NotNull
    @Column(name = "client_id", nullable = false)
    public UUID clientId;

    @NotBlank
    @Column(name = "invoice_number", nullable = false, unique = true)
    public String invoiceNumber;

    @NotNull
    @Column(name = "invoice_date", nullable = false)
    public LocalDateTime invoiceDate;

    @Column(name = "amount_cents", nullable = false)
    public long amountCents = 0;

    @Column(name = "tax_cents", nullable = false)
    public long taxCents = 0;

    @NotNull
    @Convert(converter = InvoiceStatusConverter.class)
    @Column(name = "status_raw", nullable = false)
    public InvoiceStatus status = InvoiceStatus.DRAFT;

    @Column(name = "pdf_path")
    public String pdfPath;

    @Column(name = "notes")
    public String notes = "";
}
