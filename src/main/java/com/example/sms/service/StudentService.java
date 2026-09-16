package com.example.sms.service;

import com.example.sms.exception.ConflictException;
import com.example.sms.exception.NotFoundException;
import com.example.sms.model.Student;
import com.example.sms.repository.StudentRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

/**
 * Contains all of the business logic for working with students -
 * everything that isn't purely "talk to the database" (that part is
 * StudentRepository's job) or "handle an HTTP request"
 * (that's StudentController's job).
 *
 * Keeping this logic in its own "service" layer, separate from the
 * controller and the repository, makes it much easier to unit test
 * (see StudentServiceTest) without needing a real database or a real
 * web server running.
 */
@Service
public class StudentService {

    private final StudentRepository studentRepository;

    // Spring automatically creates a StudentRepository for us and
    // "injects" it here through the constructor - we never call
    // "new StudentRepository()" ourselves anywhere.
    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    /**
     * Returns every student currently in the database.
     */
    public List<Student> all() {
        return studentRepository.findAll();
    }

    /**
     * Looks up a single student by id.
     *
     * If no student with that id exists, we throw a NotFoundException
     * instead of returning null. This means every other part of the
     * codebase that calls get(id) can safely assume they will either
     * get back a real Student object, or an exception will be thrown -
     * they never have to remember to check for null themselves.
     */
    public Student get(Long id) {
        Optional<Student> studentOrEmpty = studentRepository.findById(id);

        if (studentOrEmpty.isEmpty()) {
            throw new NotFoundException("Student not found: " + id);
        }

        return studentOrEmpty.get();
    }

    /**
     * Creates a new student, after first making sure no other student
     * already has the same email address.
     */
    public Student create(Student newStudent) {
        Optional<Student> existingStudentWithSameEmail =
                studentRepository.findByEmailIgnoreCase(newStudent.getEmail());

        if (existingStudentWithSameEmail.isPresent()) {
            throw new ConflictException("A student with that email already exists");
        }

        return studentRepository.save(newStudent);
    }

    /**
     * Updates an existing student's information.
     *
     * We look the student up first (get() will throw NotFoundException
     * if the id doesn't exist), then check that the new email isn't
     * already used by a *different* student. If everything checks out,
     * we copy each field from the incoming data onto the existing,
     * managed Student object and save it.
     */
    public Student update(Long id, Student updatedStudentData) {
        Student existingStudent = get(id);

        Optional<Student> conflictingStudent =
                studentRepository.findByEmailIgnoreCase(updatedStudentData.getEmail());

        // It's fine if the email belongs to this same student (they
        // might not have changed their email at all) - we only care if
        // it belongs to a *different* student.
        if (conflictingStudent.isPresent() && !conflictingStudent.get().getId().equals(id)) {
            throw new ConflictException("A student with that email already exists");
        }

        existingStudent.setFirstName(updatedStudentData.getFirstName());
        existingStudent.setLastName(updatedStudentData.getLastName());
        existingStudent.setEmail(updatedStudentData.getEmail());
        existingStudent.setMajor(updatedStudentData.getMajor());
        existingStudent.setGpa(updatedStudentData.getGpa());
        existingStudent.setSemester(updatedStudentData.getSemester());

        return studentRepository.save(existingStudent);
    }

    /**
     * Deletes a student by id.
     *
     * We check existsById() first so that deleting a student that
     * doesn't exist gives a clear 404 Not Found error, instead of
     * either silently doing nothing or letting a confusing database
     * error bubble up to the caller.
     */
    public void delete(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new NotFoundException("Student not found: " + id);
        }

        studentRepository.deleteById(id);
    }
}
