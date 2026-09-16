package com.example.sms.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

/**
 * Represents one student being tracked by the school.
 *
 * Like Course, this is a JPA entity, so Hibernate automatically maps
 * this class to a "students" table in PostgreSQL.
 *
 * A Student can be linked to many Courses through the Enrollment
 * entity (see Enrollment.java) - that's how we know which classes a
 * given student is taking.
 */
@Entity
@Table(
        name = "students",
        // Two students can't share the same email address. This is a
        // simple way to avoid accidentally creating duplicate student
        // records for the same person.
        uniqueConstraints = @UniqueConstraint(name = "uk_student_email", columnNames = "email")
)
public class Student {

    // Auto-generated primary key, assigned by the database.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 80)
    @Column(nullable = false)
    private String firstName;

    @NotBlank
    @Size(max = 80)
    @Column(nullable = false)
    private String lastName;

    // @Email checks that this looks like a real email address
    // (something@something.something) before it's ever saved.
    @NotBlank
    @Email
    @Size(max = 160)
    @Column(nullable = false)
    private String email;

    @NotBlank
    @Size(max = 80)
    @Column(nullable = false)
    private String major;

    // Grade point average. We use BigDecimal instead of a plain double
    // here because BigDecimal represents decimal numbers exactly, which
    // matters for something like a GPA that people expect to be precise
    // (a double can have tiny rounding errors, e.g. 3.7000000000000002).
    @DecimalMin("0.0")
    @DecimalMax("4.0")
    @Column(nullable = false, precision = 3, scale = 2)
    private BigDecimal gpa;

    // Which semester the student is currently in (1 through 8, covering
    // a typical 4-year degree).
    @NotNull
    @Min(1)
    @Max(8)
    @Column(nullable = false)
    private Integer semester;

    // Required by JPA so it can build a Student object when loading a
    // row back out of the database. We never call this directly.
    public Student() {
    }

    // The constructor we use in our own code when creating a brand new
    // student (for example in DataInitializer or in unit tests).
    public Student(
            String firstName,
            String lastName,
            String email,
            String major,
            BigDecimal gpa,
            Integer semester) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.major = major;
        this.gpa = gpa;
        this.semester = semester;
    }

    // --- Getters ---

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getMajor() {
        return major;
    }

    public BigDecimal getGpa() {
        return gpa;
    }

    public Integer getSemester() {
        return semester;
    }

    // --- Setters ---
    // Used by StudentService.update(...) when a user edits an existing
    // student's information through the API.

    public void setFirstName(String newFirstName) {
        this.firstName = newFirstName;
    }

    public void setLastName(String newLastName) {
        this.lastName = newLastName;
    }

    public void setEmail(String newEmail) {
        this.email = newEmail;
    }

    public void setMajor(String newMajor) {
        this.major = newMajor;
    }

    public void setGpa(BigDecimal newGpa) {
        this.gpa = newGpa;
    }

    public void setSemester(Integer newSemester) {
        this.semester = newSemester;
    }
}
