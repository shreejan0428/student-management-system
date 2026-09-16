package com.example.sms.controller;

import com.example.sms.dto.EnrollmentResponse;
import com.example.sms.service.EnrollmentService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Exposes the REST endpoints for enrolling students in courses and
 * recording grades, under the base path "/api/enrollments".
 */
@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    /**
     * The JSON shape the client sends us for POST /api/enrollments,
     * for example: { "studentId": 1, "courseId": 2 }.
     *
     * We use a small record here instead of reusing the Student or
     * Course entities directly, because all we actually need from the
     * client is the two ids - nothing else.
     */
    public record EnrollmentRequest(@NotNull Long studentId, @NotNull Long courseId) {
    }

    /**
     * The JSON shape the client sends us for PATCH
     * /api/enrollments/{id}/grade, for example: { "grade": "A" }.
     */
    public record GradeRequest(@NotBlank String grade) {
    }

    /**
     * POST /api/enrollments
     * Enrolls a student in a course.
     */
    @PostMapping
    public EnrollmentResponse enrollStudentInCourse(@RequestBody EnrollmentRequest request) {
        return enrollmentService.enroll(request.studentId(), request.courseId());
    }

    /**
     * GET /api/enrollments/student/{studentId}
     * Lists every course a given student is enrolled in.
     */
    @GetMapping("/student/{studentId}")
    public List<EnrollmentResponse> getEnrollmentsForStudent(@PathVariable Long studentId) {
        return enrollmentService.byStudent(studentId);
    }

    /**
     * PATCH /api/enrollments/{id}/grade
     * Records a letter grade for an existing enrollment.
     *
     * We call .toUpperCase() on the submitted grade so that a lowercase
     * "a" is treated the same as "A" - the actual validation of which
     * letters are allowed happens inside EnrollmentService.grade().
     */
    @PatchMapping("/{id}/grade")
    public EnrollmentResponse recordGrade(@PathVariable Long id, @RequestBody GradeRequest request) {
        String uppercaseGrade = request.grade().toUpperCase();
        return enrollmentService.grade(id, uppercaseGrade);
    }
}
