package com.example.Service;

import com.example.Model.Question;
import java.util.List;

public interface QuestionService {
    Question createQuestion(Question question);
    List<Question> getAllQuestions();
    Question getQuestionById(Long id);
    Question updateQuestion(Long id, Question question);
    void deleteQuestion(Long id);
}
