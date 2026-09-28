package com.example.Service;

import com.example.Model.FeedbackForm;
import com.example.Model.Question;
import com.example.Repo.FeedbackFormRepo;
import com.example.Repo.QuestionRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class QuestionServiceImpl implements QuestionService {

    @Autowired
    private QuestionRepo questionRepo;

    @Autowired
    private FeedbackFormRepo feedbackFormRepo;

    @Override
    public Question createQuestion(Question question) {
        validateQuestionInput(question);

        Long formId = question.getFeedbackForm().getId();
        FeedbackForm feedbackForm = feedbackFormRepo.findById(formId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Feedback form not found with id: " + formId));

        question.setFeedbackForm(feedbackForm);
        return questionRepo.save(question);
    }

    @Override
    public List<Question> getAllQuestions() {
        return questionRepo.findAll();
    }

    @Override
    public Question getQuestionById(Long id) {
        return questionRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Question not found with id: " + id));
    }

    @Override
    public Question updateQuestion(Long id, Question questionDetails) {
        Question existingQuestion = getQuestionById(id);
        validateQuestionInput(questionDetails);

        Long formId = questionDetails.getFeedbackForm().getId();
        FeedbackForm feedbackForm = feedbackFormRepo.findById(formId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Feedback form not found with id: " + formId));

        existingQuestion.setFeedbackForm(feedbackForm);
        existingQuestion.setQuestionText(questionDetails.getQuestionText());
        existingQuestion.setMaxRating(questionDetails.getMaxRating());

        return questionRepo.save(existingQuestion);
    }

    @Override
    public void deleteQuestion(Long id) {
        Question existingQuestion = getQuestionById(id);
        questionRepo.delete(existingQuestion);
    }

    private void validateQuestionInput(Question question) {
        if (question == null || question.getFeedbackForm() == null || question.getFeedbackForm().getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Feedback form ID is required");
        }
        if (question.getQuestionText() == null || question.getQuestionText().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Question text cannot be empty");
        }
        if (question.getMaxRating() == null || question.getMaxRating() < 1 || question.getMaxRating() > 5) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Max rating must be between 1 and 5");
        }
    }
}
