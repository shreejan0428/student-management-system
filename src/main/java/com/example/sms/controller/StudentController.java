package com.example.sms.controller;

import com.example.sms.model.Student;
import com.example.sms.service.StudentService;
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
 * Exposes the REST endpoints for managing students, under the base
 * path "/api/students".
 *
 * This controller is intentionally "thin" - it doesn't contain any
 * real business logic itself. Its only job is to:
 *   1. accept an incoming HTTP request
 *   2. hand the work off to StudentService
 *   3. wrap the result in an appropriate HTTP response
 *
 * All of the actual rules (checking for duplicate emails, throwing
 * NotFoundException, etc.) live in StudentService instead, which keeps
 * this class simple and easy to read.
 */
@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    /**
     * GET /api/students
     * Returns every student in the system.
     */
    @GetMapping
    public List<Student> getAllStudents() {
        return studentService.all();
    }

    /**
     * GET /api/students/{id}
     * Returns a single student by id, or a 404 if it doesn't exist
     * (handled automatically by GlobalExceptionHandler).
     */
    @GetMapping("/{id}")
    public Student getStudentById(@PathVariable Long id) {
        return studentService.get(id);
    }

    /**
     * POST /api/students
     * Creates a new student.
     *
     * @Valid tells Spring to run all of the validation annotations on
     * the Student class (@NotBlank, @Email, @Min, @Max, etc.) against
     * the incoming JSON body before this method even runs. If
     * validation fails, Spring throws a MethodArgumentNotValidException,
     * which GlobalExceptionHandler turns into a 400 Bad Request with a
     * readable error message.
     *
     * We return HTTP 201 Created (instead of the default 200 OK) since
     * that's the more correct status code for "a new resource was
     * successfully created".
     */
    @PostMapping
    public ResponseEntity<Student> createStudent(@Valid @RequestBody Student newStudent) {
        Student savedStudent = studentService.create(newStudent);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedStudent);
    }

    /**
     * PUT /api/students/{id}
     * Replaces an existing student's data with the given data.
     */
    @PutMapping("/{id}")
    public Student updateStudent(@PathVariable Long id, @Valid @RequestBody Student updatedStudent) {
        return studentService.update(id, updatedStudent);
    }

    /**
     * DELETE /api/students/{id}
     * Deletes a student by id.
     *
     * We return HTTP 204 No Content, which is the conventional response
     * for a successful delete that has nothing useful to send back in
     * the response body.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        studentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
