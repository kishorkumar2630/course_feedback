package com.example.Service;

import com.example.Model.Answer;
import com.example.Model.Question;
import com.example.Model.Response;
import com.example.Repo.AnswerRepo;
import com.example.Repo.QuestionRepo;
import com.example.Repo.ResponseRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class AnswerServiceImpl implements AnswerService {

    @Autowired
    private AnswerRepo answerRepo;

    @Autowired
    private ResponseRepo responseRepo;

    @Autowired
    private QuestionRepo questionRepo;

    @Override
    public Answer createAnswer(Answer answer) {
        validateAndPopulate(answer);
        return answerRepo.save(answer);
    }

    @Override
    public List<Answer> getAllAnswers() {
        return answerRepo.findAll();
    }

    @Override
    public Answer getAnswerById(Long id) {
        return answerRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Answer not found with id: " + id));
    }

    @Override
    public Answer updateAnswer(Long id, Answer answerDetails) {
        Answer existingAnswer = getAnswerById(id);
        validateAndPopulate(answerDetails);

        existingAnswer.setResponse(answerDetails.getResponse());
        existingAnswer.setQuestion(answerDetails.getQuestion());
        existingAnswer.setRating(answerDetails.getRating());

        return answerRepo.save(existingAnswer);
    }

    @Override
    public void deleteAnswer(Long id) {
        Answer existingAnswer = getAnswerById(id);
        answerRepo.delete(existingAnswer);
    }

    private void validateAndPopulate(Answer answer) {
        if (answer == null || answer.getResponse() == null || answer.getResponse().getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Response ID is required");
        }
        if (answer.getQuestion() == null || answer.getQuestion().getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Question ID is required");
        }

        Long responseId = answer.getResponse().getId();
        Long questionId = answer.getQuestion().getId();

        Response response = responseRepo.findById(responseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Response not found with id: " + responseId));

        Question question = questionRepo.findById(questionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Question not found with id: " + questionId));

        int maxRating = (question.getMaxRating() != null && question.getMaxRating() > 0) ? question.getMaxRating() : 5;
        if (answer.getRating() == null || answer.getRating() < 1 || answer.getRating() > maxRating) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rating must be between 1 and " + maxRating);
        }

        answer.setResponse(response);
        answer.setQuestion(question);
    }
}
