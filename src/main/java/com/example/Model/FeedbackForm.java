package com.example.Model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class FeedbackForm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Course course;

    private String semester;

    @Enumerated(EnumType.STRING)
    private FeedbackFormStatus status;

    private LocalDate closingDate;

    public FeedbackForm() {
    }

    public FeedbackForm(Long id, Course course, String semester, FeedbackFormStatus status, LocalDate closingDate) {
        this.id = id;
        this.course = course;
        this.semester = semester;
        this.status = status;
        this.closingDate = closingDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public FeedbackFormStatus getStatus() {
        return status;
    }

    public void setStatus(FeedbackFormStatus status) {
        this.status = status;
    }

    public LocalDate getClosingDate() {
        return closingDate;
    }

    public void setClosingDate(LocalDate closingDate) {
        this.closingDate = closingDate;
    }
}
