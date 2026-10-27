package com.pirris.model;

import java.util.UUID;

import com.pirris.model.enums.AccountRole;
import com.pirris.model.enums.AccountRoleConverter;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * A login identity belonging to a {@link StudioModel} (table {@code account}).
 *
 * <p>Not tenant-scoped itself: this is the identity layer. The {@code studioId}
 * here is a plain foreign key (the account's home studio), used to stamp the
 * tenant into the JWT at login. Login is global, so {@code email} is unique
 * across all studios.</p>
 */
@Entity
@Table(name = "account")
public class AccountModel extends AuditedModel {

    @NotNull
    @Column(name = "studio_id", nullable = false)
    public UUID studioId;

    @Email
    @NotBlank
    @Column(name = "email", nullable = false, unique = true)
    public String email;

    /** BCrypt hash; the plaintext password is never stored. */
    @NotBlank
    @Column(name = "password_hash", nullable = false)
    public String passwordHash;

    @NotBlank
    @Column(name = "display_name", nullable = false)
    public String displayName = "";

    @Convert(converter = AccountRoleConverter.class)
    @Column(name = "role_raw", nullable = false)
    public AccountRole role = AccountRole.USER;

    @Column(name = "is_active", nullable = false)
    public boolean active = true;
}
