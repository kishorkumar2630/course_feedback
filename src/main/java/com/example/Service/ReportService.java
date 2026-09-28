package com.example.Service;

import java.util.Map;

public interface ReportService {
    Map<String, Object> getInstructorReport(String instructor);
    Map<String, Object> getDepartmentReport(String department);
}
