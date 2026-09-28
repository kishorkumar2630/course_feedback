package com.example.Service;

import com.example.Model.Answer;
import java.util.List;

public interface AnswerService {
    Answer createAnswer(Answer answer);
    List<Answer> getAllAnswers();
    Answer getAnswerById(Long id);
    Answer updateAnswer(Long id, Answer answer);
    void deleteAnswer(Long id);
}
