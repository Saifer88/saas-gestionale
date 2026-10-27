package com.pirris.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

import com.pirris.model.enums.PackageKind;
import com.pirris.model.enums.PaymentMethod;
import com.pirris.model.enums.SessionStatus;

import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;

/**
 * Boots the Quarkus context so Liquibase builds the schema on H2 and Hibernate
 * validates every entity mapping against the real columns. Persists the entities
 * touched by the recent refactor (enum converters, LocalTime columns, course_id,
 * studio_id, removed denormalised columns) and reads them back.
 *
 * <p>Tenant isolation is an application-layer concern (see TenantIsolationTest);
 * here we set {@code studioId} directly, as this test only checks the O/R mapping.</p>
 */
@QuarkusTest
class SchemaMappingTest {

    private static final UUID STUDIO = UUID.fromString("11111111-1111-1111-1111-111111111111");

    @Inject
    EntityManager em;

    @Test
    @TestTransaction
    void persistsSessionWithTypedStatusAndCourseLink() {
        CourseModel course = new CourseModel();
        course.name = "Functional";
        course.weekday = 1;
        course.startTime = LocalTime.of(18, 0);
        course.studioId = STUDIO;
        course.persist();

        TrainingSessionModel session = new TrainingSessionModel();
        session.startDate = LocalDateTime.now();
        session.status = SessionStatus.PROVISIONAL;
        session.courseId = course.id;
        session.studioId = STUDIO;
        session.persist();

        em.flush();
        em.clear();

        TrainingSessionModel reloaded = TrainingSessionModel.findById(session.id);
        assertNotNull(reloaded);
        assertEquals(SessionStatus.PROVISIONAL, reloaded.status);
        assertEquals(course.id, reloaded.courseId);
        assertEquals(STUDIO, reloaded.studioId);
    }

    @Test
    @TestTransaction
    void persistsParticipantAndPackageWithTypedEnums() {
        ClientModel client = new ClientModel();
        client.firstName = "Anna";
        client.lastName = "Bianchi";
        client.joinedOn = LocalDateTime.now();
        client.studioId = STUDIO;
        client.persist();
        UUID clientId = client.id;

        LessonPackageModel pkg = new LessonPackageModel();
        pkg.clientId = clientId;
        pkg.purchasedOn = LocalDateTime.now();
        pkg.capacity = 10;
        pkg.paymentMethod = PaymentMethod.CARD;
        pkg.kind = PackageKind.TIME;
        pkg.studioId = STUDIO;
        pkg.persist();

        TrainingSessionModel session = new TrainingSessionModel();
        session.startDate = LocalDateTime.now();
        session.studioId = STUDIO;
        session.persist();

        SessionParticipantModel part = new SessionParticipantModel();
        part.sessionId = session.id;
        part.clientId = clientId;
        part.priceCents = 5000;
        part.paymentMethod = PaymentMethod.TRANSFER;
        part.studioId = STUDIO;
        part.persist();

        em.flush();
        em.clear();

        LessonPackageModel reloadedPkg = LessonPackageModel.findById(pkg.id);
        assertEquals(PaymentMethod.CARD, reloadedPkg.paymentMethod);
        assertEquals(PackageKind.TIME, reloadedPkg.kind);

        SessionParticipantModel reloadedPart = SessionParticipantModel.findById(part.id);
        assertEquals(PaymentMethod.TRANSFER, reloadedPart.paymentMethod);
    }
}
