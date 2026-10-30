package com.pirris.resource;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import java.time.LocalDateTime;
import java.util.UUID;

import com.pirris.model.ClientModel;
import com.pirris.model.ExpenseModel;
import com.pirris.repository.ClientRepository;
import com.pirris.repository.ExpenseRepository;

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
 * Proves tenant isolation over HTTP on the view resources: an expense written
 * by studio A is reachable by A and invisible to studio B (detail 404, absent
 * from the list), and a read that takes a referenced id ({@code clientId} on
 * packages) answers 404 when the id belongs to another studio.
 *
 * <p>Tests commit (no {@code @TestTransaction}) and run in order so the rows
 * written by studio A are still present when studio B queries.</p>
 */
@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ResourceTenantIsolationTest {

    private static final String STUDIO_A = "dddddddd-dddd-dddd-dddd-dddddddddddd";
    private static final String STUDIO_B = "eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee";
    private static final String MARKER = "ResourceIsolationMarker";

    private static UUID expenseId;
    private static UUID clientId;

    @Inject
    ExpenseRepository expenses;

    @Inject
    ClientRepository clients;

    @Test
    @Order(1)
    @TestSecurity(user = "a@test", roles = "user")
    @JwtSecurity(claims = @Claim(key = "studio_id", value = STUDIO_A))
    void studioAWritesAndReadsItsOwnData() {
        QuarkusTransaction.requiringNew().run(() -> {
            ExpenseModel expense = new ExpenseModel();
            expense.name = MARKER;
            expense.date = LocalDateTime.now();
            expense.amountCents = 1234L;
            expenses.persistForTenant(expense);
            expenseId = expense.id;

            ClientModel client = new ClientModel();
            client.firstName = MARKER;
            client.lastName = "A";
            client.joinedOn = LocalDateTime.now();
            clients.persistForTenant(client);
            clientId = client.id;
        });

        given().when().get("/api/expenses/{id}", expenseId)
                .then().statusCode(200)
                .body("name", is(MARKER));
        given().queryParam("clientId", clientId)
                .when().get("/api/packages")
                .then().statusCode(200);
    }

    @Test
    @Order(2)
    @TestSecurity(user = "b@test", roles = "user")
    @JwtSecurity(claims = @Claim(key = "studio_id", value = STUDIO_B))
    void studioBCannotReachStudioAData() {
        given().when().get("/api/expenses/{id}", expenseId)
                .then().statusCode(404);
        given().when().get("/api/expenses")
                .then().statusCode(200)
                .body("id", not(hasItem(expenseId.toString())));
        given().queryParam("clientId", clientId)
                .when().get("/api/packages")
                .then().statusCode(404);
    }
}
