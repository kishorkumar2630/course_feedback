package com.example;

import com.example.Model.Course;
import com.example.Model.Enrollment;
import com.example.Model.Student;
import com.example.Repo.CourseRepo;
import com.example.Repo.EnrollmentRepo;
import com.example.Repo.StudentRepo;
import com.example.Service.CourseServiceImpl;
import com.example.Service.EnrollmentServiceImpl;
import com.example.Service.StudentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ServiceTest {

    @Mock
    private StudentRepo studentRepo;

    @Mock
    private CourseRepo courseRepo;

    @Mock
    private EnrollmentRepo enrollmentRepo;

    @InjectMocks
    private StudentServiceImpl studentService;

    @InjectMocks
    private CourseServiceImpl courseService;

    @InjectMocks
    private EnrollmentServiceImpl enrollmentService;

    private Student student1;
    private Course course1;

    @BeforeEach
    void setUp() {
        student1 = new Student(1L, "STU001", "Alice Smith");
        course1 = new Course(10L, "Java Programming", "CS101", "Dr. John", "CS", "Spring 2026");
    }

    // --- STUDENT SERVICE TESTS ---

    @Test
    void testCreateStudent() {
        when(studentRepo.save(student1)).thenReturn(student1);
        Student result = studentService.createStudent(student1);
        assertNotNull(result);
        assertEquals("Alice Smith", result.getName());
    }

    @Test
    void testGetAllStudents() {
        when(studentRepo.findAll()).thenReturn(Arrays.asList(student1));
        List<Student> students = studentService.getAllStudents();
        assertEquals(1, students.size());
    }

    @Test
    void testGetStudentById_Success() {
        when(studentRepo.findById(1L)).thenReturn(Optional.of(student1));
        Student result = studentService.getStudentById(1L);
        assertEquals("STU001", result.getStudentIdentifier());
    }

    @Test
    void testGetStudentById_NotFound() {
        when(studentRepo.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> studentService.getStudentById(99L));
    }

    // --- COURSE SERVICE TESTS ---

    @Test
    void testCreateCourse() {
        when(courseRepo.save(course1)).thenReturn(course1);
        Course result = courseService.createCourse(course1);
        assertNotNull(result);
        assertEquals("Java Programming", result.getCourseName());
    }

    @Test
    void testGetCourseById_NotFound() {
        when(courseRepo.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> courseService.getCourseById(99L));
    }

    // --- ENROLLMENT SERVICE BUSINESS LOGIC TESTS ---

    @Test
    void testCreateEnrollment_Success() {
        Enrollment request = new Enrollment(null, new Student(1L, null, null), new Course(10L, null, null, null, null, null));

        when(studentRepo.findById(1L)).thenReturn(Optional.of(student1));
        when(courseRepo.findById(10L)).thenReturn(Optional.of(course1));
        when(enrollmentRepo.existsByStudentIdAndCourseId(1L, 10L)).thenReturn(false);
        when(enrollmentRepo.save(any(Enrollment.class))).thenAnswer(invocation -> {
            Enrollment saved = invocation.getArgument(0);
            saved.setId(100L);
            return saved;
        });

        Enrollment created = enrollmentService.createEnrollment(request);
        assertNotNull(created);
        assertEquals(100L, created.getId());
        assertEquals("Alice Smith", created.getStudent().getName());
        assertEquals("Java Programming", created.getCourse().getCourseName());
    }

    @Test
    void testCreateEnrollment_StudentNotFound() {
        Enrollment request = new Enrollment(null, new Student(99L, null, null), new Course(10L, null, null, null, null, null));

        when(studentRepo.findById(99L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> enrollmentService.createEnrollment(request));
        assertTrue(ex.getReason().contains("Student not found"));
    }

    @Test
    void testCreateEnrollment_CourseNotFound() {
        Enrollment request = new Enrollment(null, new Student(1L, null, null), new Course(99L, null, null, null, null, null));

        when(studentRepo.findById(1L)).thenReturn(Optional.of(student1));
        when(courseRepo.findById(99L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> enrollmentService.createEnrollment(request));
        assertTrue(ex.getReason().contains("Course not found"));
    }

    @Test
    void testCreateEnrollment_DuplicateEnrollment() {
        Enrollment request = new Enrollment(null, new Student(1L, null, null), new Course(10L, null, null, null, null, null));

        when(studentRepo.findById(1L)).thenReturn(Optional.of(student1));
        when(courseRepo.findById(10L)).thenReturn(Optional.of(course1));
        when(enrollmentRepo.existsByStudentIdAndCourseId(1L, 10L)).thenReturn(true);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> enrollmentService.createEnrollment(request));
        assertTrue(ex.getReason().contains("already enrolled"));
    }
}
