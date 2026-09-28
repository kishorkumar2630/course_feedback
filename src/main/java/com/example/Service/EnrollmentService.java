package com.example.Service;

import com.example.Model.Enrollment;
import java.util.List;

public interface EnrollmentService {
    Enrollment createEnrollment(Enrollment enrollment);
    List<Enrollment> getAllEnrollments();
    Enrollment getEnrollmentById(Long id);
    void deleteEnrollment(Long id);
}
