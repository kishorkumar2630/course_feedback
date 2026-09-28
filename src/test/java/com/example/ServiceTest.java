package com.example;

import com.example.Model.Answer;
import com.example.Model.Course;
import com.example.Model.Enrollment;
import com.example.Model.FeedbackForm;
import com.example.Model.FeedbackFormStatus;
import com.example.Model.Question;
import com.example.Model.Response;
import com.example.Model.Student;
import com.example.Repo.AnswerRepo;
import com.example.Repo.CourseRepo;
import com.example.Repo.EnrollmentRepo;
import com.example.Repo.FeedbackFormRepo;
import com.example.Repo.QuestionRepo;
import com.example.Repo.ResponseRepo;
import com.example.Repo.StudentRepo;
import com.example.Service.AnswerServiceImpl;
import com.example.Service.CourseServiceImpl;
import com.example.Service.EnrollmentServiceImpl;
import com.example.Service.FeedbackFormServiceImpl;
import com.example.Service.QuestionServiceImpl;
import com.example.Service.ResponseServiceImpl;
import com.example.Service.StudentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    @Mock
    private FeedbackFormRepo feedbackFormRepo;

    @Mock
    private QuestionRepo questionRepo;

    @Mock
    private ResponseRepo responseRepo;

    @Mock
    private AnswerRepo answerRepo;

    @InjectMocks
    private StudentServiceImpl studentService;

    @InjectMocks
    private CourseServiceImpl courseService;

    @InjectMocks
    private EnrollmentServiceImpl enrollmentService;

    @InjectMocks
    private FeedbackFormServiceImpl feedbackFormService;

    @InjectMocks
    private QuestionServiceImpl questionService;

    @InjectMocks
    private ResponseServiceImpl responseService;

    @InjectMocks
    private AnswerServiceImpl answerService;

    private Student student1;
    private Course course1;
    private FeedbackForm form1;
    private FeedbackForm publishedForm;

    @BeforeEach
    void setUp() {
        student1 = new Student(1L, "STU001", "Alice Smith");
        course1 = new Course(10L, "Java Programming", "CS101", "Dr. John", "CS", "Spring 2026");
        form1 = new FeedbackForm(1L, course1, "Spring 2026", FeedbackFormStatus.DRAFT, LocalDate.of(2026, 12, 31));
        publishedForm = new FeedbackForm(2L, course1, "Spring 2026", FeedbackFormStatus.PUBLISHED, LocalDate.now().plusDays(10));
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

    // --- FEEDBACK FORM SERVICE TESTS ---

    @Test
    void testCreateFeedbackForm_Success() {
        FeedbackForm request = new FeedbackForm(null, new Course(10L, null, null, null, null, null), "Spring 2026", null, LocalDate.of(2026, 12, 31));

        when(courseRepo.findById(10L)).thenReturn(Optional.of(course1));
        when(feedbackFormRepo.save(any(FeedbackForm.class))).thenAnswer(invocation -> {
            FeedbackForm saved = invocation.getArgument(0);
            saved.setId(50L);
            return saved;
        });

        FeedbackForm created = feedbackFormService.createFeedbackForm(request);
        assertNotNull(created);
        assertEquals(50L, created.getId());
        assertEquals(FeedbackFormStatus.DRAFT, created.getStatus());
        assertEquals("Spring 2026", created.getSemester());
    }

    @Test
    void testCreateFeedbackForm_CourseNotFound() {
        FeedbackForm request = new FeedbackForm(null, new Course(99L, null, null, null, null, null), "Spring 2026", null, LocalDate.of(2026, 12, 31));

        when(courseRepo.findById(99L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> feedbackFormService.createFeedbackForm(request));
        assertTrue(ex.getReason().contains("Course not found"));
    }

    @Test
    void testPublishFeedbackForm_Success() {
        FeedbackForm form = new FeedbackForm(50L, course1, "Spring 2026", FeedbackFormStatus.DRAFT, LocalDate.of(2026, 12, 31));

        when(feedbackFormRepo.findById(50L)).thenReturn(Optional.of(form));
        when(feedbackFormRepo.save(any(FeedbackForm.class))).thenAnswer(invocation -> invocation.getArgument(0));

        FeedbackForm published = feedbackFormService.publishFeedbackForm(50L);
        assertEquals(FeedbackFormStatus.PUBLISHED, published.getStatus());
    }

    @Test
    void testPublishFeedbackForm_InvalidStatus() {
        FeedbackForm form = new FeedbackForm(50L, course1, "Spring 2026", FeedbackFormStatus.CLOSED, LocalDate.of(2026, 12, 31));

        when(feedbackFormRepo.findById(50L)).thenReturn(Optional.of(form));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> feedbackFormService.publishFeedbackForm(50L));
        assertTrue(ex.getReason().contains("Only DRAFT feedback forms can be published"));
    }

    @Test
    void testCloseFeedbackForm_Success() {
        FeedbackForm form = new FeedbackForm(50L, course1, "Spring 2026", FeedbackFormStatus.PUBLISHED, LocalDate.of(2026, 12, 31));

        when(feedbackFormRepo.findById(50L)).thenReturn(Optional.of(form));
        when(feedbackFormRepo.save(any(FeedbackForm.class))).thenAnswer(invocation -> invocation.getArgument(0));

        FeedbackForm closed = feedbackFormService.closeFeedbackForm(50L);
        assertEquals(FeedbackFormStatus.CLOSED, closed.getStatus());
    }

    // --- QUESTION SERVICE TESTS ---

    @Test
    void testCreateQuestion_Success() {
        Question request = new Question(null, new FeedbackForm(1L, null, null, null, null), "The instructor explained the concepts clearly.", 5);

        when(feedbackFormRepo.findById(1L)).thenReturn(Optional.of(form1));
        when(questionRepo.save(any(Question.class))).thenAnswer(invocation -> {
            Question q = invocation.getArgument(0);
            q.setId(200L);
            return q;
        });

        Question created = questionService.createQuestion(request);
        assertNotNull(created);
        assertEquals(200L, created.getId());
        assertEquals("The instructor explained the concepts clearly.", created.getQuestionText());
        assertEquals(5, created.getMaxRating());
    }

    @Test
    void testCreateQuestion_FeedbackFormNotFound() {
        Question request = new Question(null, new FeedbackForm(999L, null, null, null, null), "Test question", 5);

        when(feedbackFormRepo.findById(999L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> questionService.createQuestion(request));
        assertTrue(ex.getReason().contains("Feedback form not found"));
    }

    @Test
    void testCreateQuestion_EmptyQuestionText() {
        Question request = new Question(null, new FeedbackForm(1L, null, null, null, null), "   ", 5);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> questionService.createQuestion(request));
        assertTrue(ex.getReason().contains("Question text cannot be empty"));
    }

    @Test
    void testCreateQuestion_InvalidMaxRating() {
        Question request = new Question(null, new FeedbackForm(1L, null, null, null, null), "Test question", 10);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> questionService.createQuestion(request));
        assertTrue(ex.getReason().contains("Max rating must be between 1 and 5"));
    }

    // --- RESPONSE SERVICE TESTS ---

    @Test
    void testCreateResponse_Success() {
        Response request = new Response(null, new Student(1L, null, null), new FeedbackForm(2L, null, null, null, null), null);

        when(studentRepo.findById(1L)).thenReturn(Optional.of(student1));
        when(feedbackFormRepo.findById(2L)).thenReturn(Optional.of(publishedForm));
        when(responseRepo.existsByStudentIdAndFeedbackFormId(1L, 2L)).thenReturn(false);
        when(responseRepo.save(any(Response.class))).thenAnswer(invocation -> {
            Response r = invocation.getArgument(0);
            r.setId(300L);
            return r;
        });

        Response created = responseService.createResponse(request);
        assertNotNull(created);
        assertEquals(300L, created.getId());
        assertNotNull(created.getSubmittedAt());
    }

    @Test
    void testCreateResponse_FormNotPublished() {
        Response request = new Response(null, new Student(1L, null, null), new FeedbackForm(1L, null, null, null, null), null);

        when(studentRepo.findById(1L)).thenReturn(Optional.of(student1));
        when(feedbackFormRepo.findById(1L)).thenReturn(Optional.of(form1)); // status DRAFT

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> responseService.createResponse(request));
        assertTrue(ex.getReason().contains("is not published"));
    }

    @Test
    void testCreateResponse_DeadlinePassed() {
        FeedbackForm expiredForm = new FeedbackForm(3L, course1, "Spring 2026", FeedbackFormStatus.PUBLISHED, LocalDate.now().minusDays(1));
        Response request = new Response(null, new Student(1L, null, null), new FeedbackForm(3L, null, null, null, null), null);

        when(studentRepo.findById(1L)).thenReturn(Optional.of(student1));
        when(feedbackFormRepo.findById(3L)).thenReturn(Optional.of(expiredForm));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> responseService.createResponse(request));
        assertTrue(ex.getReason().contains("deadline has passed"));
    }

    @Test
    void testCreateResponse_DuplicateSubmission() {
        Response request = new Response(null, new Student(1L, null, null), new FeedbackForm(2L, null, null, null, null), null);

        when(studentRepo.findById(1L)).thenReturn(Optional.of(student1));
        when(feedbackFormRepo.findById(2L)).thenReturn(Optional.of(publishedForm));
        when(responseRepo.existsByStudentIdAndFeedbackFormId(1L, 2L)).thenReturn(true);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> responseService.createResponse(request));
        assertTrue(ex.getReason().contains("already submitted"));
    }

    // --- ANSWER SERVICE TESTS ---

    @Test
    void testCreateAnswer_Success() {
        Response resp = new Response(300L, student1, publishedForm, LocalDateTime.now());
        Question q = new Question(200L, publishedForm, "Clear explanation?", 5);
        Answer request = new Answer(null, new Response(300L, null, null, null), new Question(200L, null, null, null), 4);

        when(responseRepo.findById(300L)).thenReturn(Optional.of(resp));
        when(questionRepo.findById(200L)).thenReturn(Optional.of(q));
        when(answerRepo.save(any(Answer.class))).thenAnswer(invocation -> {
            Answer a = invocation.getArgument(0);
            a.setId(400L);
            return a;
        });

        Answer created = answerService.createAnswer(request);
        assertNotNull(created);
        assertEquals(400L, created.getId());
        assertEquals(4, created.getRating());
    }

    @Test
    void testCreateAnswer_RatingOutOfBounds() {
        Response resp = new Response(300L, student1, publishedForm, LocalDateTime.now());
        Question q = new Question(200L, publishedForm, "Clear explanation?", 5);
        Answer request = new Answer(null, new Response(300L, null, null, null), new Question(200L, null, null, null), 10);

        when(responseRepo.findById(300L)).thenReturn(Optional.of(resp));
        when(questionRepo.findById(200L)).thenReturn(Optional.of(q));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> answerService.createAnswer(request));
        assertTrue(ex.getReason().contains("Rating must be between 1 and 5"));
    }
}
