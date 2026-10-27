package com.pirris.resource;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;

/**
 * End-to-end authentication flow over HTTP: admin provisions an account, the
 * account logs in and receives a JWT, and protected endpoints reject anonymous
 * calls. Also exercises the real token signing/verification and the HTTP auth
 * policies.
 */
@QuarkusTest
class AuthFlowTest {

    @Test
    @TestSecurity(user = "root", roles = "admin")
    void adminRegistersThenAccountLogsIn() {
        String email = "trainer+" + System.nanoTime() + "@studio.test";
        String register = """
                {
                  "studioName": "Studio Test",
                  "email": "%s",
                  "password": "secret-password",
                  "displayName": "Trainer Test",
                  "role": "user"
                }
                """.formatted(email);

        // Admin provisions studio + account.
        given().contentType(ContentType.JSON).body(register)
                .when().post("/api/admin/accounts")
                .then().statusCode(201)
                .body("accessToken", notNullValue())
                .body("studioId", notNullValue())
                .body("role", org.hamcrest.Matchers.is("user"));

        // The new account can log in (public endpoint) and gets a token.
        String login = "{\"email\":\"" + email + "\",\"password\":\"secret-password\"}";
        given().contentType(ContentType.JSON).body(login)
                .when().post("/api/auth/login")
                .then().statusCode(200)
                .body("accessToken", notNullValue());
    }

    @Test
    void loginRejectsWrongPassword() {
        String login = "{\"email\":\"nobody@studio.test\",\"password\":\"wrong\"}";
        given().contentType(ContentType.JSON).body(login)
                .when().post("/api/auth/login")
                .then().statusCode(401);
    }

    @Test
    void anonymousCannotRegister() {
        // /api/admin/accounts requires the admin role; no token -> 401.
        given().contentType(ContentType.JSON).body("{}")
                .when().post("/api/admin/accounts")
                .then().statusCode(401);
    }

    @Test
    @TestSecurity(user = "someUser", roles = "user")
    void nonAdminCannotRegister() {
        // Authenticated as a plain user -> forbidden on an admin-only endpoint.
        given().contentType(ContentType.JSON).body("{}")
                .when().post("/api/admin/accounts")
                .then().statusCode(403);
    }
}
