package com.example.sms.config;

import com.example.sms.model.AppUser;
import com.example.sms.model.Course;
import com.example.sms.model.Role;
import com.example.sms.model.Student;
import com.example.sms.repository.AppUserRepository;
import com.example.sms.repository.CourseRepository;
import com.example.sms.repository.StudentRepository;
import java.math.BigDecimal;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Populates the database with some starter data the very first time
 * the application runs, so there's something to look at right away in
 * Swagger UI without having to manually create every record by hand.
 *
 * This is purely for local development and demoing the project -
 * a real production system would NOT seed hardcoded demo passwords
 * like this (see the warning in the README about changing the seeded
 * credentials before any real deployment).
 */
@Configuration
public class DataInitializer {

    /**
     * A CommandLineRunner is a special bean that Spring Boot runs
     * automatically, exactly once, right after the application starts
     * up and before it starts accepting web requests.
     *
     * Spring automatically "injects" (passes in) the repositories and
     * the password encoder we ask for in the method parameters below -
     * we don't have to create them ourselves.
     */
    @Bean
    CommandLineRunner seedDatabase(
            AppUserRepository userRepository,
            StudentRepository studentRepository,
            CourseRepository courseRepository,
            PasswordEncoder passwordEncoder) {

        // The actual seeding logic. We wrap it in a lambda that
        // implements CommandLineRunner's run(String... args) method.
        return args -> {
            seedDemoAccounts(userRepository, passwordEncoder);
            seedDemoStudents(studentRepository);
            seedDemoCourses(courseRepository);
        };
    }

    /**
     * Creates two demo login accounts (an admin and a staff member) the
     * first time the application starts, but only if no accounts exist
     * yet. This check means restarting the app won't create duplicate
     * accounts every time.
     */
    private void seedDemoAccounts(AppUserRepository userRepository, PasswordEncoder passwordEncoder) {
        if (userRepository.count() > 0) {
            return;
        }

        String encodedAdminPassword = passwordEncoder.encode("admin123");
        userRepository.save(new AppUser("admin", encodedAdminPassword, Role.ADMIN));

        String encodedStaffPassword = passwordEncoder.encode("staff123");
        userRepository.save(new AppUser("staff", encodedStaffPassword, Role.STAFF));
    }

    /**
     * Creates a couple of example students so the /api/students
     * endpoint has something to return right away.
     */
    private void seedDemoStudents(StudentRepository studentRepository) {
        if (studentRepository.count() > 0) {
            return;
        }

        Student maya =
                new Student(
                        "Maya",
                        "Patel",
                        "maya.patel@example.com",
                        "Computer Science",
                        new BigDecimal("3.72"),
                        5);
        studentRepository.save(maya);

        Student daniel =
                new Student(
                        "Daniel",
                        "Kim",
                        "daniel.kim@example.com",
                        "Electrical Engineering",
                        new BigDecimal("3.45"),
                        4);
        studentRepository.save(daniel);
    }

    /**
     * Creates a few example courses so there's something to enroll the
     * demo students into.
     */
    private void seedDemoCourses(CourseRepository courseRepository) {
        if (courseRepository.count() > 0) {
            return;
        }

        courseRepository.save(new Course("CS3305", "Discrete Mathematics", "Computer Science", 3, 30));
        courseRepository.save(new Course("CS3345", "Data Structures", "Computer Science", 3, 25));
        courseRepository.save(new Course("EE3201", "Digital Systems", "Electrical Engineering", 3, 20));
    }
}
