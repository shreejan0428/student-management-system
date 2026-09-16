package com.example.sms.dto;

import com.example.sms.model.Enrollment;
import java.time.LocalDateTime;

/**
 * This is a "DTO" (Data Transfer Object) - a simple class whose only
 * job is to control exactly what gets sent back to the client in a
 * JSON response.
 *
 * Why not just return the Enrollment entity directly from the
 * controller? A few reasons:
 *   1. Enrollment only stores student and course *ids* internally
 *      (through the @ManyToOne relationships), but it's much more
 *      useful for whoever is calling the API to also see the
 *      student's name and the course code without making a second
 *      request.
 *   2. Returning entities directly can accidentally leak internal
 *      database details or cause infinite loops when Jackson (the
 *      JSON library) tries to serialize lazy-loaded relationships.
 *
 * A Java "record" is a compact way to write a class that is just a
 * bundle of final fields plus a constructor, getters, equals(),
 * hashCode(), and toString() - all generated for us automatically.
 * That makes it a great fit for a simple, read-only DTO like this one.
 */
public record EnrollmentResponse(
        Long id,
        Long studentId,
        String studentName,
        Long courseId,
        String courseCode,
        LocalDateTime enrolledAt,
        String grade) {

    /**
     * Builds an EnrollmentResponse out of a full Enrollment entity.
     *
     * We put this conversion logic here (instead of in the service or
     * controller) so that there is exactly one place in the whole
     * codebase that knows how to turn an Enrollment into the JSON shape
     * we send back to clients.
     */
    public static EnrollmentResponse from(Enrollment enrollment) {
        String fullStudentName =
                enrollment.getStudent().getFirstName() + " " + enrollment.getStudent().getLastName();

        return new EnrollmentResponse(
                enrollment.getId(),
                enrollment.getStudent().getId(),
                fullStudentName,
                enrollment.getCourse().getId(),
                enrollment.getCourse().getCode(),
                enrollment.getEnrolledAt(),
                enrollment.getGrade());
    }
}
