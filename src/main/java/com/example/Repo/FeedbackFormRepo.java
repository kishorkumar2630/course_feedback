package com.example.Repo;

import com.example.Model.FeedbackForm;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedbackFormRepo extends JpaRepository<FeedbackForm, Long> {
    List<FeedbackForm> findByCourseId(Long courseId);
}
