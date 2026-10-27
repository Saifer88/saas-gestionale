package com.pirris.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import com.pirris.model.ClientModel;
import com.pirris.repository.ClientRepository;

import io.quarkus.narayana.jta.QuarkusTransaction;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.quarkus.test.security.jwt.Claim;
import io.quarkus.test.security.jwt.JwtSecurity;
import jakarta.inject.Inject;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * Proves application-layer tenant isolation: a client created through
 * {@link ClientRepository} under one studio is invisible to another studio,
 * because every repository read is constrained to the current studio taken from
 * the JWT and every write is stamped with it.
 *
 * <p>Tests commit (no {@code @TestTransaction}) and run in order so the row
 * written by studio A is still present when studio B queries.</p>
 */
@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TenantIsolationTest {

    private static final String STUDIO_A = "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa";
    private static final String STUDIO_B = "bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb";
    private static final String MARKER = "IsolationMarker";

    @Inject
    ClientRepository clients;

    @Test
    @Order(1)
    @TestSecurity(user = "a@test", roles = "user")
    @JwtSecurity(claims = @Claim(key = "studio_id", value = STUDIO_A))
    void studioACreatesAClient() {
        QuarkusTransaction.requiringNew().run(() -> {
            ClientModel c = new ClientModel();
            c.firstName = MARKER;
            c.lastName = "A";
            c.joinedOn = LocalDateTime.now();
            clients.persistForTenant(c);
        });

        QuarkusTransaction.requiringNew().run(() -> {
            long seenByA = clients.search(MARKER).size();
            assertEquals(1, seenByA, "studio A deve vedere il proprio cliente");
        });
    }

    @Test
    @Order(2)
    @TestSecurity(user = "b@test", roles = "user")
    @JwtSecurity(claims = @Claim(key = "studio_id", value = STUDIO_B))
    void studioBCannotSeeStudioAClient() {
        QuarkusTransaction.requiringNew().run(() -> {
            long seenByB = clients.search(MARKER).size();
            assertEquals(0, seenByB, "studio B NON deve vedere i clienti di studio A");
            assertTrue(clients.findByIdForTenant(
                    java.util.UUID.fromString(STUDIO_A)).isEmpty(),
                    "nessun dato di un altro studio deve essere raggiungibile");
        });
    }
}
