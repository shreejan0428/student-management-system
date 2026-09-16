package com.example.sms.repository;

import com.example.sms.model.Enrollment;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Handles all direct database access for Enrollment rows (the link
 * between a Student and a Course).
 */
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    /**
     * Returns true if this exact student + course pair is already
     * enrolled. EnrollmentService uses this to block duplicate
     * enrollments before they happen.
     */
    boolean existsByStudentIdAndCourseId(Long studentId, Long courseId);

    /**
     * Counts how many students are currently enrolled in a given
     * course. EnrollmentService compares this against the course's
     * capacity to decide whether the course is full.
     */
    long countByCourseId(Long courseId);

    /**
     * Returns every enrollment for a given student, e.g. so we can show
     * "here are all the classes this student is taking."
     */
    List<Enrollment> findByStudentId(Long studentId);

    /**
     * Returns every enrollment for a given course, e.g. so we can show
     * a class roster.
     */
    List<Enrollment> findByCourseId(Long courseId);

    /**
     * Looks up a single enrollment by its id, but also eagerly loads
     * ("join fetch"es) the related Student and Course in the very same
     * SQL query.
     *
     * Why do we need this instead of just using the normal findById()
     * that JpaRepository already gives us for free? Because Student
     * and Course are marked as FetchType.LAZY on the Enrollment entity
     * (see Enrollment.java). That means if we just used plain
     * findById(), Hibernate would only load the enrollment itself, and
     * trying to read enrollment.getStudent().getFirstName() afterwards
     * could fail (a LazyInitializationException) once the database
     * session that loaded it has already closed.
     *
     * This custom JPQL query ("Java Persistence Query Language", which
     * looks a lot like SQL but works on entity/field names instead of
     * table/column names) tells Hibernate to load everything we need
     * up front, in one round trip to the database. We use this in
     * EnrollmentService.grade(...), which needs the full student and
     * course details to build the EnrollmentResponse afterward.
     */
    @Query("select e from Enrollment e join fetch e.student join fetch e.course where e.id = :id")
    Optional<Enrollment> findDetailedById(@Param("id") Long id);
}
