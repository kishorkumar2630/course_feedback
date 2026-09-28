package com.example.Service;

import com.example.Model.Answer;
import com.example.Model.Course;
import com.example.Model.FeedbackForm;
import com.example.Model.Question;
import com.example.Repo.AnswerRepo;
import com.example.Repo.CourseRepo;
import com.example.Repo.FeedbackFormRepo;
import com.example.Repo.QuestionRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private CourseRepo courseRepo;

    @Autowired
    private FeedbackFormRepo feedbackFormRepo;

    @Autowired
    private QuestionRepo questionRepo;

    @Autowired
    private AnswerRepo answerRepo;

    @Override
    public Map<String, Object> getInstructorReport(String instructor) {
        List<Course> courses = courseRepo.findByInstructor(instructor);
        if (courses == null || courses.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No courses found for instructor: " + instructor);
        }

        List<Map<String, Object>> courseReportsList = new ArrayList<>();
        double instructorTotalSum = 0.0;
        int ratedCoursesCount = 0;

        for (Course course : courses) {
            Map<String, Object> cMap = buildCourseReportMap(course);
            double courseRating = (Double) cMap.get("rating");
            boolean hasData = (Boolean) cMap.get("hasData");

            if (hasData) {
                instructorTotalSum += courseRating;
                ratedCoursesCount++;
            }
            cMap.remove("hasData");
            courseReportsList.add(cMap);
        }

        double overallInstructorAverage = 0.0;
        if (ratedCoursesCount > 0) {
            overallInstructorAverage = Math.round((instructorTotalSum / ratedCoursesCount) * 100.0) / 100.0;
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("instructor", instructor);
        result.put("courses", courseReportsList);
        result.put("overallInstructorAverage", overallInstructorAverage);
        result.put("overallAverage", overallInstructorAverage);

        return result;
    }

    @Override
    public Map<String, Object> getDepartmentReport(String department) {
        List<Course> courses = courseRepo.findByDepartment(department);
        if (courses == null || courses.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No courses found for department: " + department);
        }

        List<Map<String, Object>> courseReportsList = new ArrayList<>();
        double departmentTotalSum = 0.0;
        int ratedCoursesCount = 0;

        for (Course course : courses) {
            Map<String, Object> cMap = buildCourseReportMap(course);
            double courseRating = (Double) cMap.get("rating");
            boolean hasData = (Boolean) cMap.get("hasData");

            if (hasData) {
                departmentTotalSum += courseRating;
                ratedCoursesCount++;
            }
            cMap.remove("hasData");
            courseReportsList.add(cMap);
        }

        double overallDepartmentAverage = 0.0;
        if (ratedCoursesCount > 0) {
            overallDepartmentAverage = Math.round((departmentTotalSum / ratedCoursesCount) * 100.0) / 100.0;
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("department", department);
        result.put("courses", courseReportsList);
        result.put("overallDepartmentAverage", overallDepartmentAverage);
        result.put("overallAverage", overallDepartmentAverage);

        return result;
    }

    private Map<String, Object> buildCourseReportMap(Course course) {
        List<FeedbackForm> forms = feedbackFormRepo.findByCourseId(course.getId());
        List<Map<String, Object>> formReports = new ArrayList<>();

        double courseFormsRatingSum = 0.0;
        int formsWithDataCount = 0;

        if (forms != null) {
            for (FeedbackForm form : forms) {
                FormRatingResult formResult = calculateFormRating(form.getId());
                Map<String, Object> fMap = new LinkedHashMap<>();
                fMap.put("feedbackFormId", form.getId());
                fMap.put("semester", form.getSemester());
                fMap.put("status", form.getStatus());
                fMap.put("rating", formResult.overallRating);
                formReports.add(fMap);

                if (formResult.hasData) {
                    courseFormsRatingSum += formResult.overallRating;
                    formsWithDataCount++;
                }
            }
        }

        double courseRating = 0.0;
        if (formsWithDataCount > 0) {
            courseRating = Math.round((courseFormsRatingSum / formsWithDataCount) * 100.0) / 100.0;
        }

        Map<String, Object> cMap = new LinkedHashMap<>();
        cMap.put("courseId", course.getId());
        cMap.put("courseName", course.getCourseName());
        cMap.put("courseCode", course.getCourseCode());
        cMap.put("instructor", course.getInstructor());
        cMap.put("department", course.getDepartment());
        cMap.put("semester", course.getSemester());
        cMap.put("rating", courseRating);
        cMap.put("feedbackForms", formReports);
        cMap.put("hasData", formsWithDataCount > 0);

        return cMap;
    }

    private FormRatingResult calculateFormRating(Long feedbackFormId) {
        List<Question> questions = questionRepo.findByFeedbackFormId(feedbackFormId);
        if (questions == null || questions.isEmpty()) {
            return new FormRatingResult(0.0, false);
        }

        double totalQuestionRatingSum = 0.0;
        int answeredQuestionsCount = 0;

        for (Question question : questions) {
            List<Answer> answers = answerRepo.findByQuestionId(question.getId());
            if (answers != null && !answers.isEmpty()) {
                double sum = 0.0;
                for (Answer answer : answers) {
                    if (answer.getRating() != null) {
                        sum += answer.getRating();
                    }
                }
                double avgRating = sum / answers.size();
                totalQuestionRatingSum += avgRating;
                answeredQuestionsCount++;
            }
        }

        if (answeredQuestionsCount == 0) {
            return new FormRatingResult(0.0, false);
        }

        double overallRating = Math.round((totalQuestionRatingSum / answeredQuestionsCount) * 100.0) / 100.0;
        return new FormRatingResult(overallRating, true);
    }

    private static class FormRatingResult {
        final double overallRating;
        final boolean hasData;

        FormRatingResult(double overallRating, boolean hasData) {
            this.overallRating = overallRating;
            this.hasData = hasData;
        }
    }
}
