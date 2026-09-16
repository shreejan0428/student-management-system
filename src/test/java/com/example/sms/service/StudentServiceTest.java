package com.example.sms.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.sms.exception.ConflictException;
import com.example.sms.exception.NotFoundException;
import com.example.sms.model.Student;
import com.example.sms.repository.StudentRepository;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Unit tests for StudentService.
 *
 * These are "unit" tests, meaning we test StudentService completely on
 * its own, without a real database and without starting the whole
 * Spring Boot application. We do this by giving StudentService a
 * "mock" (fake, controllable) StudentRepository instead of a real one,
 * using Mockito. That lets us decide exactly what the repository
 * "finds" for each test, and then check that StudentService reacts
 * correctly.
 */
class StudentServiceTest {

    // @Mock tells Mockito to create a fake StudentRepository for us.
    // Calling any method on it (like repo.findById(...)) does nothing
    // useful unless we've explicitly told it what to return with
    // when(...).thenReturn(...), as we do in each test below.
    @Mock
    private StudentRepository studentRepository;

    // The real object we are testing - it's real, only its dependency
    // (studentRepository) is fake.
    private StudentService studentService;

    @BeforeEach
    void setUp() {
        // Turns the @Mock fields above into actual working mock objects.
        MockitoAnnotations.openMocks(this);
        studentService = new StudentService(studentRepository);
    }

    @Test
    void getThrowsNotFoundExceptionWhenStudentDoesNotExist() {
        when(studentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> studentService.get(1L));
    }

    @Test
    void createThrowsConflictExceptionWhenEmailIsAlreadyTaken() {
        Student existingStudent =
                new Student("Amy", "Lee", "amy@example.com", "Computer Science", new BigDecimal("3.5"), 2);
        Student newStudentWithSameEmail =
                new Student("Amy", "Lopez", "amy@example.com", "Mathematics", new BigDecimal("3.1"), 1);

        when(studentRepository.findByEmailIgnoreCase("amy@example.com"))
                .thenReturn(Optional.of(existingStudent));

        assertThrows(ConflictException.class, () -> studentService.create(newStudentWithSameEmail));

        // We also make sure the new student was never actually saved,
        // since the whole point of this test is that creation should
        // be blocked.
        verify(studentRepository, never()).save(any());
    }

    @Test
    void createSavesTheStudentWhenEmailIsUnique() {
        Student newStudent =
                new Student("Amy", "Lee", "amy@example.com", "Computer Science", new BigDecimal("3.5"), 2);

        when(studentRepository.findByEmailIgnoreCase("amy@example.com")).thenReturn(Optional.empty());
        when(studentRepository.save(newStudent)).thenReturn(newStudent);

        Student savedStudent = studentService.create(newStudent);

        assertEquals("amy@example.com", savedStudent.getEmail());
        verify(studentRepository).save(newStudent);
    }

    @Test
    void deleteThrowsNotFoundExceptionWhenStudentDoesNotExist() {
        when(studentRepository.existsById(9L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> studentService.delete(9L));

        // Since the student doesn't exist, deleteById should never be
        // called at all.
        verify(studentRepository, never()).deleteById(any());
    }
}
