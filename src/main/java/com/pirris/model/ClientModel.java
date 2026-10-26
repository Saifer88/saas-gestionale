package com.pirris.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

/**
 * Client profile with personal details, medical anamnesis and scheduling
 * preferences (table {@code client}).
 */
@Entity
@Table(name = "client")
public class ClientModel extends AuditedModel {

    @NotBlank
    @Column(name = "first_name", nullable = false)
    public String firstName;

    @NotBlank
    @Column(name = "last_name", nullable = false)
    public String lastName;

    @Column(name = "phone", nullable = false)
    public String phone = "";

    @Column(name = "email", nullable = false)
    public String email = "";

    @Column(name = "tax_code", nullable = false)
    public String taxCode = "";

    @Column(name = "billing_address", nullable = false)
    public String billingAddress = "";

    @Column(name = "birth_date")
    public LocalDate birthDate;

    @Column(name = "notes", nullable = false)
    public String notes = "";

    @Column(name = "anamnesis", nullable = false)
    public String anamnesis = "";

    @Column(name = "physical_analysis", nullable = false)
    public String physicalAnalysis = "";

    @Column(name = "preferred_service_id")
    public UUID preferredServiceId;

    @Column(name = "preferred_rate_id")
    public UUID preferredRateId;

    @Column(name = "joined_on", nullable = false)
    public LocalDateTime joinedOn;

    @Column(name = "is_archived", nullable = false)
    public boolean archived = false;
}
