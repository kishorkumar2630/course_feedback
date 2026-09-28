package com.example.Service;

import com.example.Model.Course;
import com.example.Model.FeedbackForm;
import com.example.Model.FeedbackFormStatus;
import com.example.Repo.CourseRepo;
import com.example.Repo.FeedbackFormRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class FeedbackFormServiceImpl implements FeedbackFormService {

    @Autowired
    private FeedbackFormRepo feedbackFormRepo;

    @Autowired
    private CourseRepo courseRepo;

    @Override
    public FeedbackForm createFeedbackForm(FeedbackForm feedbackForm) {
        if (feedbackForm == null || feedbackForm.getCourse() == null || feedbackForm.getCourse().getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Course ID is required for feedback form");
        }

        Long courseId = feedbackForm.getCourse().getId();
        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found with id: " + courseId));

        feedbackForm.setCourse(course);
        feedbackForm.setStatus(FeedbackFormStatus.DRAFT);
        return feedbackFormRepo.save(feedbackForm);
    }

    @Override
    public List<FeedbackForm> getAllFeedbackForms() {
        return feedbackFormRepo.findAll();
    }

    @Override
    public FeedbackForm getFeedbackFormById(Long id) {
        return feedbackFormRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Feedback form not found with id: " + id));
    }

    @Override
    public FeedbackForm updateFeedbackForm(Long id, FeedbackForm feedbackFormDetails) {
        FeedbackForm existingForm = getFeedbackFormById(id);

        if (feedbackFormDetails.getCourse() != null && feedbackFormDetails.getCourse().getId() != null) {
            Long courseId = feedbackFormDetails.getCourse().getId();
            Course course = courseRepo.findById(courseId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found with id: " + courseId));
            existingForm.setCourse(course);
        }

        if (feedbackFormDetails.getSemester() != null) {
            existingForm.setSemester(feedbackFormDetails.getSemester());
        }
        if (feedbackFormDetails.getClosingDate() != null) {
            existingForm.setClosingDate(feedbackFormDetails.getClosingDate());
        }

        return feedbackFormRepo.save(existingForm);
    }

    @Override
    public void deleteFeedbackForm(Long id) {
        FeedbackForm existingForm = getFeedbackFormById(id);
        feedbackFormRepo.delete(existingForm);
    }

    @Override
    public FeedbackForm publishFeedbackForm(Long id) {
        FeedbackForm form = getFeedbackFormById(id);

        if (form.getStatus() != FeedbackFormStatus.DRAFT) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only DRAFT feedback forms can be published");
        }

        form.setStatus(FeedbackFormStatus.PUBLISHED);
        return feedbackFormRepo.save(form);
    }

    @Override
    public FeedbackForm closeFeedbackForm(Long id) {
        FeedbackForm form = getFeedbackFormById(id);
        form.setStatus(FeedbackFormStatus.CLOSED);
        return feedbackFormRepo.save(form);
    }
}
