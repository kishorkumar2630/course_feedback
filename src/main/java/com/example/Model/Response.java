package com.example.Model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Response {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Student student;

    @ManyToOne
    private FeedbackForm feedbackForm;

    private LocalDateTime submittedAt;

    public Response() {
    }

    public Response(Long id, Student student, FeedbackForm feedbackForm, LocalDateTime submittedAt) {
        this.id = id;
        this.student = student;
        this.feedbackForm = feedbackForm;
        this.submittedAt = submittedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public FeedbackForm getFeedbackForm() {
        return feedbackForm;
    }

    public void setFeedbackForm(FeedbackForm feedbackForm) {
        this.feedbackForm = feedbackForm;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }
}
