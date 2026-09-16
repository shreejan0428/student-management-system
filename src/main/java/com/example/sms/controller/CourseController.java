package com.example.sms.controller;

import com.example.sms.model.Course;
import com.example.sms.service.CourseService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Exposes the REST endpoints for managing courses, under the base path
 * "/api/courses".
 *
 * This follows the exact same "thin controller" pattern as
 * StudentController - see the comments there for more detail on why
 * the business logic lives in CourseService instead of here.
 */
@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    /**
     * GET /api/courses
     * Returns every course in the system.
     */
    @GetMapping
    public List<Course> getAllCourses() {
        return courseService.all();
    }

    /**
     * GET /api/courses/{id}
     * Returns a single course by id.
     */
    @GetMapping("/{id}")
    public Course getCourseById(@PathVariable Long id) {
        return courseService.get(id);
    }

    /**
     * POST /api/courses
     * Creates a new course. Returns HTTP 201 Created on success.
     */
    @PostMapping
    public ResponseEntity<Course> createCourse(@Valid @RequestBody Course newCourse) {
        Course savedCourse = courseService.create(newCourse);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCourse);
    }

    /**
     * PUT /api/courses/{id}
     * Replaces an existing course's data with the given data.
     */
    @PutMapping("/{id}")
    public Course updateCourse(@PathVariable Long id, @Valid @RequestBody Course updatedCourse) {
        return courseService.update(id, updatedCourse);
    }

    /**
     * DELETE /api/courses/{id}
     * Deletes a course by id. Returns HTTP 204 No Content on success.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id) {
        courseService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
