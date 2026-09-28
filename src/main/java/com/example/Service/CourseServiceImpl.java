package com.example.Service;

import com.example.Model.Course;
import com.example.Repo.CourseRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CourseServiceImpl implements CourseService {

    @Autowired
    private CourseRepo courseRepo;

    @Override
    public Course createCourse(Course course) {
        return courseRepo.save(course);
    }

    @Override
    public List<Course> getAllCourses() {
        return courseRepo.findAll();
    }

    @Override
    public Course getCourseById(Long id) {
        return courseRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found with id: " + id));
    }

    @Override
    public Course updateCourse(Long id, Course courseDetails) {
        Course existingCourse = getCourseById(id);
        existingCourse.setCourseName(courseDetails.getCourseName());
        existingCourse.setCourseCode(courseDetails.getCourseCode());
        existingCourse.setInstructor(courseDetails.getInstructor());
        existingCourse.setDepartment(courseDetails.getDepartment());
        existingCourse.setSemester(courseDetails.getSemester());
        return courseRepo.save(existingCourse);
    }

    @Override
    public void deleteCourse(Long id) {
        Course existingCourse = getCourseById(id);
        courseRepo.delete(existingCourse);
    }
}
