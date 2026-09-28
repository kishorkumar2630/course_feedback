package com.example.Repo;

import com.example.Model.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseRepo extends JpaRepository<Course, Long> {
    List<Course> findByInstructor(String instructor);
    List<Course> findByDepartment(String department);
}
