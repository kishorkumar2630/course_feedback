package com.example.Service;

import com.example.Model.Course;
import com.example.Model.Enrollment;
import com.example.Model.Student;
import com.example.Repo.CourseRepo;
import com.example.Repo.EnrollmentRepo;
import com.example.Repo.StudentRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class EnrollmentServiceImpl implements EnrollmentService {

    @Autowired
    private EnrollmentRepo enrollmentRepo;

    @Autowired
    private StudentRepo studentRepo;

    @Autowired
    private CourseRepo courseRepo;

    @Override
    public Enrollment createEnrollment(Enrollment enrollment) {
        if (enrollment == null || enrollment.getStudent() == null || enrollment.getStudent().getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Student ID is required for enrollment");
        }
        if (enrollment.getCourse() == null || enrollment.getCourse().getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Course ID is required for enrollment");
        }

        Long studentId = enrollment.getStudent().getId();
        Long courseId = enrollment.getCourse().getId();

        // STEP 1: Check whether student exists
        Student student = studentRepo.findById(studentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found with id: " + studentId));

        // STEP 2: Check whether course exists
        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found with id: " + courseId));

        // STEP 3: Check duplicate enrollment
        if (enrollmentRepo.existsByStudentIdAndCourseId(studentId, courseId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Student is already enrolled in this course");
        }

        // STEP 4: Save enrollment
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        return enrollmentRepo.save(enrollment);
    }

    @Override
    public List<Enrollment> getAllEnrollments() {
        return enrollmentRepo.findAll();
    }

    @Override
    public Enrollment getEnrollmentById(Long id) {
        return enrollmentRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Enrollment not found with id: " + id));
    }

    @Override
    public void deleteEnrollment(Long id) {
        Enrollment existingEnrollment = getEnrollmentById(id);
        enrollmentRepo.delete(existingEnrollment);
    }
}
