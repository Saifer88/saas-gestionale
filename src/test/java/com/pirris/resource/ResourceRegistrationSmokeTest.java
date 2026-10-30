package com.pirris.resource;

import static io.restassured.RestAssured.given;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.quarkus.test.security.jwt.Claim;
import io.quarkus.test.security.jwt.JwtSecurity;

/**
 * Smoke test of the view resources: an authenticated GET on each implemented
 * read endpoint answers 200, proving the {@code *ResourceImpl} is registered
 * (a missing class-level {@code @Path} gives 404) and the tenant-scoped read
 * path works end to end. Stub-only endpoints are not called here.
 */
@QuarkusTest
class ResourceRegistrationSmokeTest {

    /** A studio id not used by other tests: every list is empty but valid. */
    private static final String STUDIO = "cccccccc-cccc-cccc-cccc-cccccccccccc";

    @Test
    @TestSecurity(user = "smoke@test", roles = "user")
    @JwtSecurity(claims = @Claim(key = "studio_id", value = STUDIO))
    void expensesListIsServed() {
        given().when().get("/api/expenses").then().statusCode(200);
        given().queryParam("from", "2025-01-01").queryParam("to", "2025-12-31")
                .when().get("/api/expenses").then().statusCode(200);
    }

    @Test
    @TestSecurity(user = "smoke@test", roles = "user")
    @JwtSecurity(claims = @Claim(key = "studio_id", value = STUDIO))
    void servicesListIsServed() {
        given().when().get("/api/services").then().statusCode(200);
        given().queryParam("includeArchived", true)
                .when().get("/api/services").then().statusCode(200);
    }

    @Test
    @TestSecurity(user = "smoke@test", roles = "user")
    @JwtSecurity(claims = @Claim(key = "studio_id", value = STUDIO))
    void packagesListIsServed() {
        given().when().get("/api/packages").then().statusCode(200);
    }

    @Test
    @TestSecurity(user = "smoke@test", roles = "user")
    @JwtSecurity(claims = @Claim(key = "studio_id", value = STUDIO))
    void coursesListIsServed() {
        given().when().get("/api/courses").then().statusCode(200);
    }

    @Test
    @TestSecurity(user = "smoke@test", roles = "user")
    @JwtSecurity(claims = @Claim(key = "studio_id", value = STUDIO))
    void agendaCalendarIsServed() {
        given().when().get("/api/agenda").then().statusCode(200);
        given().queryParam("view", "DAY").queryParam("reference", "2025-03-15")
                .when().get("/api/agenda").then().statusCode(200);
        given().queryParam("view", "MONTH").queryParam("reference", "2025-03-15")
                .when().get("/api/agenda").then().statusCode(200);
    }

    @Test
    void anonymousIsRejected() {
        given().when().get("/api/expenses").then().statusCode(401);
    }
}
