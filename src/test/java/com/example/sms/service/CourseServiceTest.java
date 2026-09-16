package com.example.sms.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.sms.exception.ConflictException;
import com.example.sms.exception.NotFoundException;
import com.example.sms.model.Course;
import com.example.sms.repository.CourseRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Unit tests for CourseService, following the exact same mocking
 * pattern used in StudentServiceTest - see the comments there for a
 * full explanation of how and why we use Mockito here.
 */
class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    private CourseService courseService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        courseService = new CourseService(courseRepository);
    }

    @Test
    void getThrowsNotFoundExceptionWhenCourseDoesNotExist() {
        when(courseRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> courseService.get(1L));
    }

    @Test
    void createThrowsConflictExceptionWhenCourseCodeIsAlreadyTaken() {
        Course existingCourse = new Course("CS1", "Intro to Programming", "Computer Science", 3, 20);
        Course newCourseWithSameCode =
                new Course("CS1", "Intro to Computer Science", "Computer Science", 3, 25);

        when(courseRepository.findByCodeIgnoreCase("CS1")).thenReturn(Optional.of(existingCourse));

        assertThrows(ConflictException.class, () -> courseService.create(newCourseWithSameCode));
        verify(courseRepository, never()).save(any());
    }

    @Test
    void updateSavesTheNewValuesWhenCourseCodeIsUnique() {
        Course existingCourse = new Course("CS1", "Intro to Programming", "Computer Science", 3, 20);
        Course updatedCourseData =
                new Course("CS1", "Intro to Computer Science", "Computer Science", 3, 30);

        when(courseRepository.findById(1L)).thenReturn(Optional.of(existingCourse));
        // No other course has this code, so the update should be allowed.
        when(courseRepository.findByCodeIgnoreCase("CS1")).thenReturn(Optional.empty());
        // save() just returns whatever it was given, like a real
        // repository would after persisting the changes.
        when(courseRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Course result = courseService.update(1L, updatedCourseData);

        assertEquals("Intro to Computer Science", result.getName());
        assertEquals(30, result.getCapacity());
    }

    @Test
    void deleteThrowsNotFoundExceptionWhenCourseDoesNotExist() {
        when(courseRepository.existsById(9L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> courseService.delete(9L));
        verify(courseRepository, never()).deleteById(any());
    }
}
