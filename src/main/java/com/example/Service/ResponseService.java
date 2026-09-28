package com.example.Service;

import com.example.Model.Response;
import java.util.List;

public interface ResponseService {
    Response createResponse(Response response);
    List<Response> getAllResponses();
    Response getResponseById(Long id);
    void deleteResponse(Long id);
}
