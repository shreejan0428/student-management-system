package com.example.sms.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.sms.exception.ConflictException;
import com.example.sms.model.Course;
import com.example.sms.model.Student;
import com.example.sms.repository.CourseRepository;
import com.example.sms.repository.EnrollmentRepository;
import com.example.sms.repository.StudentRepository;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Unit tests for EnrollmentService, focused on the two business rules
 * that matter most for enrollment: no duplicate enrollments, and no
 * enrolling past a course's capacity.
 *
 * Just like StudentServiceTest and CourseServiceTest, we give
 * EnrollmentService three fake (mocked) repositories instead of real
 * ones, so these tests run instantly and don't need a database.
 */
class EnrollmentServiceTest {

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private CourseRepository courseRepository;

    private EnrollmentService enrollmentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        enrollmentService =
                new EnrollmentService(enrollmentRepository, studentRepository, courseRepository);
    }

    @Test
    void enrollThrowsConflictExceptionWhenStudentIsAlreadyEnrolled() {
        Student student =
                new Student("Ada", "Byron", "ada@example.com", "Computer Science", new BigDecimal("3.9"), 2);
        Course course = new Course("CS1", "Intro to Programming", "Computer Science", 3, 20);

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(courseRepository.findById(2L)).thenReturn(Optional.of(course));
        // Pretend this student/course pair is already enrolled.
        when(enrollmentRepository.existsByStudentIdAndCourseId(1L, 2L)).thenReturn(true);

        assertThrows(ConflictException.class, () -> enrollmentService.enroll(1L, 2L));

        // The whole point of this rule is to prevent a second
        // enrollment row from ever being saved.
        verify(enrollmentRepository, never()).save(any());
    }

    @Test
    void enrollThrowsConflictExceptionWhenCourseIsFull() {
        Student student =
                new Student("Ada", "Byron", "ada@example.com", "Computer Science", new BigDecimal("3.9"), 2);
        // This course only has room for 20 students.
        Course course = new Course("CS1", "Intro to Programming", "Computer Science", 3, 20);

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(courseRepository.findById(2L)).thenReturn(Optional.of(course));
        // Pretend the course already has exactly 20 students enrolled,
        // which is at its capacity.
        when(enrollmentRepository.countByCourseId(2L)).thenReturn(20L);

        assertThrows(ConflictException.class, () -> enrollmentService.enroll(1L, 2L));

        verify(enrollmentRepository, never()).save(any());
    }
}
