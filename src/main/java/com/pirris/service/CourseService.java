package com.pirris.service;

import java.util.List;
import java.util.UUID;

import com.pirris.dto.course.CourseDetailDto;
import com.pirris.dto.course.CourseDraftDto;
import com.pirris.dto.course.CourseEnrollmentDraftDto;
import com.pirris.dto.course.CourseParticipantDto;
import com.pirris.dto.course.CourseSummaryDto;
import com.pirris.mapper.CourseMapper;
import com.pirris.model.CourseModel;
import com.pirris.repository.CourseParticipantRepository;
import com.pirris.repository.CourseRepository;
import com.pirris.resource.error.NotFoundException;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * Business logic for the Corsi view: group course templates and enrollments.
 * A course is only the weekly recurrence template; its concrete occurrences are
 * {@code training_session} rows with {@code course_id} (generation is Phase 3).
 * All data access goes through tenant-aware repositories.
 *
 * <p>Reads are implemented; writes are stubs until the write flow is built.</p>
 */
@ApplicationScoped
public class CourseService {

    @Inject
    CourseRepository courses;

    @Inject
    CourseParticipantRepository participants;

    @Inject
    CourseMapper mapper;

    /** Courses of the studio by weekday and time; inactive ones only on request. */
    public List<CourseSummaryDto> list(boolean includeInactive) {
        return mapper.toSummaries(courses.list(includeInactive));
    }

    /** One course with its active enrollments; 404 when not in the studio. */
    public CourseDetailDto detail(UUID id) {
        CourseModel course = requireCourse(id);
        return mapper.toDetail(course, mapper.toParticipantDtos(participants.listActiveByCourse(course.id)));
    }

    /** Creates a course template from the draft. */
    public CourseDetailDto create(CourseDraftDto draft) {
        // TODO: tenant-check serviceId, map the draft onto a new CourseModel and persist it.
        throw new UnsupportedOperationException("CourseService.create not yet implemented");
    }

    /** Updates a course template from the draft. */
    public CourseDetailDto update(UUID id, CourseDraftDto draft) {
        // TODO: require the course in the studio, tenant-check serviceId, apply the draft.
        throw new UnsupportedOperationException("CourseService.update not yet implemented");
    }

    /** Deactivates a course (soft, {@code active = false}). */
    public void deactivate(UUID id) {
        // TODO: require the course in the studio, set active = false.
        throw new UnsupportedOperationException("CourseService.deactivate not yet implemented");
    }

    /** Reactivates a course. */
    public void activate(UUID id) {
        // TODO: require the course in the studio, set active = true.
        throw new UnsupportedOperationException("CourseService.activate not yet implemented");
    }

    /** Enrolls a client in a course. */
    public CourseParticipantDto enroll(UUID courseId, CourseEnrollmentDraftDto draft) {
        // TODO: tenant-check course/client/package, capacity check, persist the enrollment.
        throw new UnsupportedOperationException("CourseService.enroll not yet implemented");
    }

    /** Unenrolls a participant (soft, {@code active = false}). */
    public void unenroll(UUID courseId, UUID participantId) {
        // TODO: require the enrollment in the studio and belonging to courseId, set active = false.
        throw new UnsupportedOperationException("CourseService.unenroll not yet implemented");
    }

    private CourseModel requireCourse(UUID id) {
        return courses.findByIdForTenant(id)
                .orElseThrow(() -> NotFoundException.of("Course", id));
    }
}
