package com.pirris.model;

import java.util.UUID;

import com.pirris.model.enums.PaymentMethod;
import com.pirris.model.enums.PaymentMethodConverter;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

/**
 * Link between a {@link TrainingSessionModel} and a {@link ClientModel}
 * attending it, with per-participant payment tracking
 * (table {@code session_participant}).
 */
@Entity
@Table(name = "session_participant")
public class SessionParticipantModel extends TenantModel {

    @NotNull
    @Column(name = "session_id", nullable = false)
    public UUID sessionId;

    @NotNull
    @Column(name = "client_id", nullable = false)
    public UUID clientId;

    @Column(name = "price_cents", nullable = false)
    public long priceCents = 0;

    @Column(name = "package_id")
    public UUID packageId;

    @Convert(converter = PaymentMethodConverter.class)
    @Column(name = "payment_method_raw", nullable = false)
    public PaymentMethod paymentMethod = PaymentMethod.CASH;

    @Column(name = "is_paid", nullable = false)
    public boolean paid = false;

    @Column(name = "is_black", nullable = false)
    public boolean black = false;
}
