package com.example.sms.repository;

import com.example.sms.model.Course;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Handles all direct database access for Course rows.
 *
 * Just like StudentRepository, extending JpaRepository<Course, Long>
 * gives us save(), findById(), findAll(), deleteById(), existsById(),
 * etc. automatically, with no implementation code needed from us.
 */
public interface CourseRepository extends JpaRepository<Course, Long> {

    /**
     * Looks up a course by its course code, ignoring uppercase/lowercase
     * differences. Spring Data JPA generates the SQL for this
     * automatically based on the method name, similar to
     * StudentRepository.findByEmailIgnoreCase.
     *
     * CourseService uses this to make sure two courses never end up
     * with the same code (e.g. "CS3345").
     */
    Optional<Course> findByCodeIgnoreCase(String code);
}
