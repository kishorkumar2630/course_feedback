package com.example.Service;

import com.example.Model.FeedbackForm;
import java.util.List;

public interface FeedbackFormService {
    FeedbackForm createFeedbackForm(FeedbackForm feedbackForm);
    List<FeedbackForm> getAllFeedbackForms();
    FeedbackForm getFeedbackFormById(Long id);
    FeedbackForm updateFeedbackForm(Long id, FeedbackForm feedbackForm);
    void deleteFeedbackForm(Long id);
    FeedbackForm publishFeedbackForm(Long id);
    FeedbackForm closeFeedbackForm(Long id);
}
