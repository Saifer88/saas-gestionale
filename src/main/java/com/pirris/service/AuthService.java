package com.pirris.service;

import java.time.Duration;
import java.util.Set;
import java.util.UUID;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.pirris.dto.auth.ChangePasswordRequest;
import com.pirris.dto.auth.LoginRequest;
import com.pirris.dto.auth.RegisterAccountRequest;
import com.pirris.dto.auth.TokenResponse;
import com.pirris.model.AccountModel;
import com.pirris.model.StudioModel;
import com.pirris.model.enums.AccountRole;
import com.pirris.security.TenantContext;

import io.quarkus.elytron.security.common.BcryptUtil;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotAuthorizedException;

/**
 * Authentication and account provisioning.
 *
 * <p>{@link AccountModel}/{@link StudioModel} are the identity layer and are not
 * tenant-scoped, so these operations run without a resolved tenant (login must
 * work before a studio is known). Passwords are stored only as BCrypt hashes;
 * tokens are signed JWTs carrying the studio id and role.</p>
 */
@ApplicationScoped
public class AuthService {

    @ConfigProperty(name = "gestionale.jwt.duration-seconds", defaultValue = "28800")
    long tokenDurationSeconds;

    @ConfigProperty(name = "mp.jwt.verify.issuer")
    String issuer;

    /**
     * Admin-only: provisions an account, creating its studio when needed.
     * Fails on a duplicate email.
     */
    @Transactional
    public TokenResponse register(RegisterAccountRequest req) {
        String email = normalizeEmail(req.email());
        if (AccountModel.find("email", email).firstResult() != null) {
            throw new BadRequestException("Esiste già un account con questa email.");
        }

        StudioModel studio;
        if (req.studioId() != null) {
            studio = StudioModel.findById(req.studioId());
            if (studio == null) {
                throw new BadRequestException("Studio inesistente: " + req.studioId());
            }
        } else {
            if (req.studioName() == null || req.studioName().isBlank()) {
                throw new BadRequestException("Indicare studioId esistente o studioName per crearne uno nuovo.");
            }
            studio = new StudioModel();
            studio.name = req.studioName().trim();
            studio.persist();
        }

        AccountModel account = new AccountModel();
        account.studioId = studio.id;
        account.email = email;
        account.passwordHash = BcryptUtil.bcryptHash(req.password());
        account.displayName = req.displayName().trim();
        account.role = AccountRole.fromRaw(req.role());
        account.persist();

        return issueToken(account);
    }

    /** Verifies credentials and issues a token; generic error to avoid user enumeration. */
    public TokenResponse login(LoginRequest req) {
        AccountModel account = AccountModel.find("email", normalizeEmail(req.email())).firstResult();
        if (account == null || !account.active
                || !BcryptUtil.matches(req.password(), account.passwordHash)) {
            throw new NotAuthorizedException("Credenziali non valide.");
        }
        return issueToken(account);
    }

    /** Changes the authenticated account's own password after re-checking the current one. */
    @Transactional
    public void changePassword(UUID accountId, ChangePasswordRequest req) {
        AccountModel account = AccountModel.findById(accountId);
        if (account == null) {
            throw new NotAuthorizedException("Account non trovato.");
        }
        if (!BcryptUtil.matches(req.currentPassword(), account.passwordHash)) {
            throw new BadRequestException("La password attuale non è corretta.");
        }
        account.passwordHash = BcryptUtil.bcryptHash(req.newPassword());
    }

    private TokenResponse issueToken(AccountModel account) {
        String role = account.role.raw();
        String token = Jwt.issuer(issuer)
                .upn(account.email)
                .subject(account.id.toString())
                .groups(Set.of(role))
                .claim(TenantContext.STUDIO_CLAIM, account.studioId.toString())
                .claim("name", account.displayName)
                .expiresIn(Duration.ofSeconds(tokenDurationSeconds))
                .sign();
        return new TokenResponse(token, tokenDurationSeconds,
                account.studioId.toString(), role, account.displayName);
    }

    private static String normalizeEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase();
    }
}
