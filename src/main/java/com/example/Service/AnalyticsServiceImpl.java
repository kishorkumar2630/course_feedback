package com.example.Service;

import com.example.Model.Answer;
import com.example.Model.FeedbackForm;
import com.example.Model.Question;
import com.example.Repo.AnswerRepo;
import com.example.Repo.FeedbackFormRepo;
import com.example.Repo.QuestionRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
public class AnalyticsServiceImpl implements AnalyticsService {

    @Autowired
    private FeedbackFormRepo feedbackFormRepo;

    @Autowired
    private QuestionRepo questionRepo;

    @Autowired
    private AnswerRepo answerRepo;

    @Override
    public Map<String, Object> getFormAnalytics(Long feedbackFormId) {
        FeedbackForm form = feedbackFormRepo.findById(feedbackFormId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Feedback form not found with id: " + feedbackFormId));

        List<Question> questions = questionRepo.findByFeedbackFormId(feedbackFormId);
        List<Map<String, Object>> questionAnalyticsList = new ArrayList<>();

        double totalQuestionRatingSum = 0.0;
        int answeredQuestionsCount = 0;

        for (Question question : questions) {
            List<Answer> answers = answerRepo.findByQuestionId(question.getId());
            double avgRating = 0.0;

            if (answers != null && !answers.isEmpty()) {
                double sum = 0.0;
                for (Answer answer : answers) {
                    if (answer.getRating() != null) {
                        sum += answer.getRating();
                    }
                }
                avgRating = Math.round((sum / answers.size()) * 100.0) / 100.0;
                totalQuestionRatingSum += avgRating;
                answeredQuestionsCount++;
            }

            Map<String, Object> qMap = new LinkedHashMap<>();
            qMap.put("questionId", question.getId());
            qMap.put("questionText", question.getQuestionText());
            qMap.put("maxRating", question.getMaxRating());
            qMap.put("averageRating", avgRating);
            questionAnalyticsList.add(qMap);
        }

        double overallRating = 0.0;
        if (answeredQuestionsCount > 0) {
            overallRating = Math.round((totalQuestionRatingSum / answeredQuestionsCount) * 100.0) / 100.0;
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("feedbackFormId", form.getId());
        result.put("status", form.getStatus());
        result.put("semester", form.getSemester());
        result.put("closingDate", form.getClosingDate());
        result.put("course", form.getCourse());
        result.put("questionAnalytics", questionAnalyticsList);
        result.put("overallRating", overallRating);

        return result;
    }
}
