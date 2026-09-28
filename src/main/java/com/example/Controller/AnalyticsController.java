package com.example.Controller;

import com.example.Service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    @GetMapping("/form/{feedbackFormId}")
    public ResponseEntity<Map<String, Object>> getFormAnalytics(@PathVariable Long feedbackFormId) {
        Map<String, Object> analytics = analyticsService.getFormAnalytics(feedbackFormId);
        return new ResponseEntity<>(analytics, HttpStatus.OK);
    }
}
