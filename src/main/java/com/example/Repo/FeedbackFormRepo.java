package com.example.Repo;

import com.example.Model.FeedbackForm;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedbackFormRepo extends JpaRepository<FeedbackForm, Long> {
}
