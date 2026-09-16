package com.example.sms.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Represents one course that students can enroll in, for example
 * "CS3345 - Data Structures".
 *
 * This class is a JPA "entity", which just means Spring Data JPA /
 * Hibernate will automatically turn each Course object into a row in
 * the "courses" table in PostgreSQL, and turn each row back into a
 * Course object when we read it back out.
 */
@Entity
@Table(
        name = "courses",
        // No two courses can share the same course code (e.g. two
        // different courses both called "CS3345"). The database enforces
        // this for us, on top of the check we do in CourseService.
        uniqueConstraints = @UniqueConstraint(name = "uk_course_code", columnNames = "code")
)
public class Course {

    // Auto-generated primary key. The database assigns this, we don't.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Short course identifier, like "CS3345". @NotBlank means it can't
    // be null or empty/whitespace-only when submitted through the API.
    @NotBlank
    @Size(max = 20)
    @Column(nullable = false)
    private String code;

    // Full course title, like "Data Structures".
    @NotBlank
    @Size(max = 160)
    @Column(nullable = false)
    private String name;

    // Which department offers the course, e.g. "Computer Science".
    @NotBlank
    @Size(max = 80)
    @Column(nullable = false)
    private String department;

    // How many credit hours the course is worth. We only allow 1-6
    // credits, since that covers basically every real course.
    @Min(1)
    @Max(6)
    @Column(nullable = false)
    private Integer credits;

    // Maximum number of students who can enroll in this course. This is
    // used by EnrollmentService to reject new enrollments once the
    // course is full.
    @Min(1)
    @Max(500)
    @Column(nullable = false)
    private Integer capacity;

    // JPA needs a no-argument constructor to build objects when reading
    // rows back out of the database. We don't call this ourselves.
    public Course() {
    }

    // The constructor we actually use when creating a brand new course
    // in our own code (for example, in DataInitializer or tests).
    public Course(String code, String name, String department, Integer credits, Integer capacity) {
        this.code = code;
        this.name = name;
        this.department = department;
        this.credits = credits;
        this.capacity = capacity;
    }

    // --- Getters ---
    // These let other classes (like CourseService and the JSON
    // serializer that turns objects into API responses) read each field.

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getDepartment() {
        return department;
    }

    public Integer getCredits() {
        return credits;
    }

    public Integer getCapacity() {
        return capacity;
    }

    // --- Setters ---
    // These let CourseService update an existing Course's fields when a
    // user calls the "update course" endpoint (PUT /api/courses/{id}).

    public void setCode(String newCode) {
        this.code = newCode;
    }

    public void setName(String newName) {
        this.name = newName;
    }

    public void setDepartment(String newDepartment) {
        this.department = newDepartment;
    }

    public void setCredits(Integer newCredits) {
        this.credits = newCredits;
    }

    public void setCapacity(Integer newCapacity) {
        this.capacity = newCapacity;
    }
}
