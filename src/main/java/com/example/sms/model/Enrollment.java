package com.example.sms.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;

/**
 * Represents the link between one Student and one Course - in other
 * words, "this student is taking this course".
 *
 * This is what's called a "join table" or "association entity" in
 * database design. Instead of a Student having a simple list of
 * Courses, we need an entity in the middle because we also want to
 * store extra information about the enrollment itself, like when the
 * student enrolled and what grade they eventually got.
 */
@Entity
@Table(
        name = "enrollments",
        // A student should never be able to enroll in the exact same
        // course twice. This unique constraint on the pair of columns
        // (student_id, course_id) enforces that at the database level,
        // and EnrollmentService also checks for this before saving.
        uniqueConstraints =
        @UniqueConstraint(
                name = "uk_student_course",
                columnNames = {"student_id", "course_id"})
)
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // FetchType.LAZY means Hibernate will NOT automatically load the
    // full Student object every time it loads an Enrollment - it will
    // only load it when we actually call getStudent(). This is a
    // performance optimization: most of the time we just need the
    // student's id, not their whole record.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    // The exact date and time the enrollment was created. Set
    // automatically in the constructor below - the caller never has to
    // provide this themselves.
    @Column(nullable = false)
    private LocalDateTime enrolledAt;

    // The letter grade the student earned, e.g. "A", "B", "F". This
    // starts out as null (no grade yet) and is filled in later using
    // EnrollmentService.grade(...).
    @Column(length = 2)
    private String grade;

    // Required by JPA for loading rows back out of the database.
    public Enrollment() {
    }

    // Creates a brand new enrollment "right now". We don't need to pass
    // in enrolledAt because we always want it to be the current time.
    public Enrollment(Student student, Course course) {
        this.student = student;
        this.course = course;
        this.enrolledAt = LocalDateTime.now();
    }

    // --- Getters ---

    public Long getId() {
        return id;
    }

    public Student getStudent() {
        return student;
    }

    public Course getCourse() {
        return course;
    }

    public LocalDateTime getEnrolledAt() {
        return enrolledAt;
    }

    public String getGrade() {
        return grade;
    }

    // The only field that ever changes after an enrollment is created
    // is the grade, so that's the only setter we need.
    public void setGrade(String newGrade) {
        this.grade = newGrade;
    }
}
