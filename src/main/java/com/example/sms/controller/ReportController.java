package com.example.sms.controller;

import com.example.sms.model.Student;
import com.example.sms.service.StudentService;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Exposes simple, read-only reporting endpoints, under the base path
 * "/api/reports".
 *
 * This is a small example of a controller that combines information
 * from an existing service in a new way, without needing its own
 * dedicated service or repository.
 */
@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final StudentService studentService;

    public ReportController(StudentService studentService) {
        this.studentService = studentService;
    }

    /**
     * The JSON shape returned by GET /api/reports/summary, for example:
     * { "students": 42, "averageGpa": 3.41 }
     */
    public record Summary(long students, double averageGpa) {
    }

    /**
     * GET /api/reports/summary
     * Returns the total number of students and their average GPA.
     */
    @GetMapping("/summary")
    public Summary getSummary() {
        List<Student> allStudents = studentService.all();

        double averageGpa = calculateAverageGpa(allStudents);

        return new Summary(allStudents.size(), averageGpa);
    }

    /**
     * Calculates the average GPA across a list of students, rounded to
     * two decimal places. Returns 0 if there are no students at all,
     * to avoid dividing by zero.
     */
    private double calculateAverageGpa(List<Student> students) {
        double average =
                students.stream()
                        .map(Student::getGpa)
                        .mapToDouble(BigDecimal::doubleValue)
                        .average()
                        .orElse(0.0);

        // Rounds to 2 decimal places, e.g. 3.4055555 -> 3.41
        return Math.round(average * 100.0) / 100.0;
    }
}
