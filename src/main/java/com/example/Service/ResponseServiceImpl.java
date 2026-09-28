package com.example.Service;

import com.example.Model.FeedbackForm;
import com.example.Model.FeedbackFormStatus;
import com.example.Model.Response;
import com.example.Model.Student;
import com.example.Repo.FeedbackFormRepo;
import com.example.Repo.ResponseRepo;
import com.example.Repo.StudentRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ResponseServiceImpl implements ResponseService {

    @Autowired
    private ResponseRepo responseRepo;

    @Autowired
    private StudentRepo studentRepo;

    @Autowired
    private FeedbackFormRepo feedbackFormRepo;

    @Override
    public Response createResponse(Response response) {
        if (response == null || response.getStudent() == null || response.getStudent().getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Student ID is required");
        }
        if (response.getFeedbackForm() == null || response.getFeedbackForm().getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Feedback form ID is required");
        }

        Long studentId = response.getStudent().getId();
        Long formId = response.getFeedbackForm().getId();

        Student student = studentRepo.findById(studentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found with id: " + studentId));

        FeedbackForm feedbackForm = feedbackFormRepo.findById(formId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Feedback form not found with id: " + formId));

        if (feedbackForm.getStatus() != FeedbackFormStatus.PUBLISHED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Feedback form is not published");
        }

        if (feedbackForm.getClosingDate() != null && LocalDate.now().isAfter(feedbackForm.getClosingDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Feedback deadline has passed");
        }

        if (responseRepo.existsByStudentIdAndFeedbackFormId(studentId, formId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Student has already submitted feedback for this form");
        }

        response.setStudent(student);
        response.setFeedbackForm(feedbackForm);
        response.setSubmittedAt(LocalDateTime.now());

        return responseRepo.save(response);
    }

    @Override
    public List<Response> getAllResponses() {
        return responseRepo.findAll();
    }

    @Override
    public Response getResponseById(Long id) {
        return responseRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Response not found with id: " + id));
    }

    @Override
    public void deleteResponse(Long id) {
        Response existingResponse = getResponseById(id);
        responseRepo.delete(existingResponse);
    }
}
