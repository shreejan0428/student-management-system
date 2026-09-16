package com.example.sms.service;

import com.example.sms.dto.EnrollmentResponse;
import com.example.sms.exception.ConflictException;
import com.example.sms.exception.NotFoundException;
import com.example.sms.model.Course;
import com.example.sms.model.Enrollment;
import com.example.sms.model.Student;
import com.example.sms.repository.CourseRepository;
import com.example.sms.repository.EnrollmentRepository;
import com.example.sms.repository.StudentRepository;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Contains the business logic for enrolling students into courses and
 * recording grades.
 *
 * This class needs all three repositories (students, courses, and
 * enrollments) because enrolling a student in a course touches all
 * three: we need to check that the student exists, that the course
 * exists, and then create/update a row in the enrollments table.
 */
@Service
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    public EnrollmentService(
            EnrollmentRepository enrollmentRepository,
            StudentRepository studentRepository,
            CourseRepository courseRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
    }

    /**
     * Enrolls a student into a course, after checking:
     *   1. the student actually exists
     *   2. the course actually exists
     *   3. the student isn't already enrolled in this course
     *   4. the course still has room (isn't at capacity yet)
     *
     * @Transactional means all of the database work in this method
     * happens as a single, all-or-nothing unit. If anything goes wrong
     * partway through, none of the changes are saved. This matters here
     * because, without it, it would theoretically be possible for two
     * requests to run at almost the same time and both "see" the course
     * as having room, resulting in more enrollments than the course's
     * capacity allows.
     */
    @Transactional
    public EnrollmentResponse enroll(Long studentId, Long courseId) {
        Student student = findStudentOrThrow(studentId);
        Course course = findCourseOrThrow(courseId);

        boolean alreadyEnrolled =
                enrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId);
        if (alreadyEnrolled) {
            throw new ConflictException("Student is already enrolled in this course");
        }

        long currentEnrollmentCount = enrollmentRepository.countByCourseId(courseId);
        if (currentEnrollmentCount >= course.getCapacity()) {
            throw new ConflictException("Course is at capacity");
        }

        Enrollment newEnrollment = new Enrollment(student, course);
        Enrollment savedEnrollment = enrollmentRepository.save(newEnrollment);

        return EnrollmentResponse.from(savedEnrollment);
    }

    /**
     * Returns every course a given student is enrolled in, converted
     * into the simpler EnrollmentResponse shape (see
     * dto/EnrollmentResponse.java for why we don't just return the raw
     * Enrollment entities).
     *
     * @Transactional(readOnly = true) is a small performance hint to
     * Hibernate: since we're only reading data here and never changing
     * it, Hibernate can skip some of the extra bookkeeping it normally
     * does to track changes for saving later.
     */
    @Transactional(readOnly = true)
    public List<EnrollmentResponse> byStudent(Long studentId) {
        if (!studentRepository.existsById(studentId)) {
            throw new NotFoundException("Student not found: " + studentId);
        }

        List<Enrollment> enrollments = enrollmentRepository.findByStudentId(studentId);

        return enrollments.stream()
                .map(EnrollmentResponse::from)
                .toList();
    }

    /**
     * Records a letter grade for an existing enrollment.
     *
     * We use findDetailedById() here (instead of the plain findById()
     * that JpaRepository gives us for free) because EnrollmentResponse.from()
     * needs to read the student's name and the course's code, and those
     * are lazy-loaded relationships - see the big comment on
     * EnrollmentRepository.findDetailedById() for the full explanation.
     */
    @Transactional
    public EnrollmentResponse grade(Long enrollmentId, String letterGrade) {
        Optional<Enrollment> enrollmentOrEmpty = enrollmentRepository.findDetailedById(enrollmentId);

        if (enrollmentOrEmpty.isEmpty()) {
            throw new NotFoundException("Enrollment not found: " + enrollmentId);
        }

        Set<String> allowedGrades = Set.of("A", "B", "C", "D", "F");
        if (!allowedGrades.contains(letterGrade)) {
            throw new ConflictException("Grade must be A, B, C, D, or F");
        }

        Enrollment enrollment = enrollmentOrEmpty.get();
        enrollment.setGrade(letterGrade);

        // Note: we don't need to call enrollmentRepository.save(enrollment)
        // here. Because this method is @Transactional, and "enrollment"
        // was loaded from the database within this same transaction,
        // Hibernate is already tracking it. Any changes made to it
        // (like setGrade above) get automatically written back to the
        // database when the transaction commits at the end of this
        // method. This is called "dirty checking".
        return EnrollmentResponse.from(enrollment);
    }

    /**
     * Small private helper used by enroll() to look up a student and
     * turn "not found" into a clear NotFoundException.
     */
    private Student findStudentOrThrow(Long studentId) {
        Optional<Student> studentOrEmpty = studentRepository.findById(studentId);

        if (studentOrEmpty.isEmpty()) {
            throw new NotFoundException("Student not found: " + studentId);
        }

        return studentOrEmpty.get();
    }

    /**
     * Small private helper used by enroll() to look up a course and
     * turn "not found" into a clear NotFoundException.
     */
    private Course findCourseOrThrow(Long courseId) {
        Optional<Course> courseOrEmpty = courseRepository.findById(courseId);

        if (courseOrEmpty.isEmpty()) {
            throw new NotFoundException("Course not found: " + courseId);
        }

        return courseOrEmpty.get();
    }
}
