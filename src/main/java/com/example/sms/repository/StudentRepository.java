package com.example.sms.repository;

import com.example.sms.model.Student;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * This interface handles all direct database access for Student rows.
 *
 * We don't have to write any implementation code here at all!
 * By extending JpaRepository<Student, Long>, Spring Data JPA
 * automatically generates an implementation behind the scenes that
 * gives us methods like save(), findById(), findAll(), deleteById(),
 * existsById(), and count() for free.
 *
 * The "Long" is the type of Student's primary key (its id field).
 */
public interface StudentRepository extends JpaRepository<Student, Long> {

    /**
     * Looks up a student by email address, ignoring uppercase/lowercase
     * differences (so "Amy@Example.com" and "amy@example.com" are
     * treated as the same email).
     *
     * Spring Data JPA is smart enough to generate the actual SQL query
     * for this just from the method name - we don't have to write any
     * SQL ourselves. It sees "findBy" + "Email" + "IgnoreCase" and
     * builds the equivalent of:
     *   SELECT * FROM students WHERE LOWER(email) = LOWER(?)
     *
     * We use this in StudentService to check whether a student with
     * this email already exists before creating or updating one.
     */
    Optional<Student> findByEmailIgnoreCase(String email);
}
