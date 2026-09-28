package com.example.Repo;

import com.example.Model.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepo extends JpaRepository<Question, Long> {
    List<Question> findByFeedbackFormId(Long feedbackFormId);
}
