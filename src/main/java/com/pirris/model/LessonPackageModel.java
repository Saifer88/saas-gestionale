package com.pirris.model;

import java.time.LocalDateTime;
import java.util.UUID;

import com.pirris.model.enums.PackageKind;
import com.pirris.model.enums.PackageKindConverter;
import com.pirris.model.enums.PaymentMethod;
import com.pirris.model.enums.PaymentMethodConverter;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

/**
 * A prepaid lesson or time package bought by a client
 * (table {@code lesson_package}). This table carries no audit columns.
 */
@Entity
@Table(name = "lesson_package")
public class LessonPackageModel extends BaseModel {

    @NotNull
    @Column(name = "client_id", nullable = false)
    public UUID clientId;

    @Column(name = "purchased_on", nullable = false)
    public LocalDateTime purchasedOn;

    @Column(name = "price_cents", nullable = false)
    public long priceCents = 0;

    /** Number of lessons for a {@code LESSONS} package. */
    @Column(name = "capacity", nullable = false)
    public int capacity = 10;

    @Column(name = "expires_on")
    public LocalDateTime expiresOn;

    @Column(name = "notes", nullable = false)
    public String notes = "";

    @Convert(converter = PaymentMethodConverter.class)
    @Column(name = "payment_method_raw", nullable = false)
    public PaymentMethod paymentMethod = PaymentMethod.CASH;

    @Column(name = "invoice_date")
    public LocalDateTime invoiceDate;

    @Column(name = "is_black", nullable = false)
    public boolean black = false;

    @Convert(converter = PackageKindConverter.class)
    @Column(name = "kind_raw", nullable = false)
    public PackageKind kind = PackageKind.LESSONS;
}
