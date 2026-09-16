package com.example.sms.service;

import com.example.sms.exception.ConflictException;
import com.example.sms.exception.NotFoundException;
import com.example.sms.model.Course;
import com.example.sms.repository.CourseRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

/**
 * Contains all of the business logic for working with courses.
 *
 * This class follows exactly the same pattern as StudentService - see
 * the comments there for a full explanation of why the logic lives
 * here instead of in the controller or repository.
 */
@Service
public class CourseService {

    private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    /**
     * Returns every course currently in the database.
     */
    public List<Course> all() {
        return courseRepository.findAll();
    }

    /**
     * Looks up a single course by id, throwing NotFoundException if it
     * doesn't exist.
     */
    public Course get(Long id) {
        Optional<Course> courseOrEmpty = courseRepository.findById(id);

        if (courseOrEmpty.isEmpty()) {
            throw new NotFoundException("Course not found: " + id);
        }

        return courseOrEmpty.get();
    }

    /**
     * Creates a new course, after making sure no other course already
     * uses the same course code.
     */
    public Course create(Course newCourse) {
        Optional<Course> existingCourseWithSameCode =
                courseRepository.findByCodeIgnoreCase(newCourse.getCode());

        if (existingCourseWithSameCode.isPresent()) {
            throw new ConflictException("A course with that code already exists");
        }

        return courseRepository.save(newCourse);
    }

    /**
     * Updates an existing course's information, making sure the new
     * course code isn't already used by a *different* course.
     */
    public Course update(Long id, Course updatedCourseData) {
        Course existingCourse = get(id);

        Optional<Course> conflictingCourse =
                courseRepository.findByCodeIgnoreCase(updatedCourseData.getCode());

        if (conflictingCourse.isPresent() && !conflictingCourse.get().getId().equals(id)) {
            throw new ConflictException("A course with that code already exists");
        }

        existingCourse.setCode(updatedCourseData.getCode());
        existingCourse.setName(updatedCourseData.getName());
        existingCourse.setDepartment(updatedCourseData.getDepartment());
        existingCourse.setCredits(updatedCourseData.getCredits());
        existingCourse.setCapacity(updatedCourseData.getCapacity());

        return courseRepository.save(existingCourse);
    }

    /**
     * Deletes a course by id, throwing NotFoundException first if it
     * doesn't exist.
     */
    public void delete(Long id) {
        if (!courseRepository.existsById(id)) {
            throw new NotFoundException("Course not found: " + id);
        }

        courseRepository.deleteById(id);
    }
}
