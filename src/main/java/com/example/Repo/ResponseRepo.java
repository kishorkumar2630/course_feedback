package com.example.Repo;

import com.example.Model.Response;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResponseRepo extends JpaRepository<Response, Long> {
    boolean existsByStudentIdAndFeedbackFormId(Long studentId, Long feedbackFormId);
}
