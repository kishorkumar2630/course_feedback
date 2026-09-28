package com.example.Controller;

import com.example.Model.FeedbackForm;
import com.example.Service.FeedbackFormService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feedback-forms")
public class FeedbackFormController {

    @Autowired
    private FeedbackFormService feedbackFormService;

    @PostMapping("/create")
    public ResponseEntity<FeedbackForm> createFeedbackForm(@RequestBody FeedbackForm feedbackForm) {
        FeedbackForm createdForm = feedbackFormService.createFeedbackForm(feedbackForm);
        return new ResponseEntity<>(createdForm, HttpStatus.CREATED);
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<FeedbackForm>> getAllFeedbackForms() {
        List<FeedbackForm> forms = feedbackFormService.getAllFeedbackForms();
        return new ResponseEntity<>(forms, HttpStatus.OK);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<FeedbackForm> getFeedbackFormById(@PathVariable Long id) {
        FeedbackForm form = feedbackFormService.getFeedbackFormById(id);
        return new ResponseEntity<>(form, HttpStatus.OK);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<FeedbackForm> updateFeedbackForm(@PathVariable Long id, @RequestBody FeedbackForm feedbackForm) {
        FeedbackForm updatedForm = feedbackFormService.updateFeedbackForm(id, feedbackForm);
        return new ResponseEntity<>(updatedForm, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteFeedbackForm(@PathVariable Long id) {
        feedbackFormService.deleteFeedbackForm(id);
        return new ResponseEntity<>("Feedback form deleted successfully", HttpStatus.OK);
    }

    @PutMapping("/publish/{id}")
    public ResponseEntity<FeedbackForm> publishFeedbackForm(@PathVariable Long id) {
        FeedbackForm publishedForm = feedbackFormService.publishFeedbackForm(id);
        return new ResponseEntity<>(publishedForm, HttpStatus.OK);
    }

    @PutMapping("/close/{id}")
    public ResponseEntity<FeedbackForm> closeFeedbackForm(@PathVariable Long id) {
        FeedbackForm closedForm = feedbackFormService.closeFeedbackForm(id);
        return new ResponseEntity<>(closedForm, HttpStatus.OK);
    }
}
