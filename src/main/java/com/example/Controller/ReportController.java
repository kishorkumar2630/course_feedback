package com.example.Controller;

import com.example.Service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping("/instructor/{instructor}")
    public ResponseEntity<Map<String, Object>> getInstructorReport(@PathVariable String instructor) {
        Map<String, Object> report = reportService.getInstructorReport(instructor);
        return new ResponseEntity<>(report, HttpStatus.OK);
    }

    @GetMapping("/department/{department}")
    public ResponseEntity<Map<String, Object>> getDepartmentReport(@PathVariable String department) {
        Map<String, Object> report = reportService.getDepartmentReport(department);
        return new ResponseEntity<>(report, HttpStatus.OK);
    }
}
