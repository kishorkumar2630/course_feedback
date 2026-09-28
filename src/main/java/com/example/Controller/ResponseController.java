package com.example.Controller;

import com.example.Model.Response;
import com.example.Service.ResponseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/responses")
public class ResponseController {

    @Autowired
    private ResponseService responseService;

    @PostMapping("/create")
    public ResponseEntity<Response> createResponse(@RequestBody Response response) {
        Response createdResponse = responseService.createResponse(response);
        return new ResponseEntity<>(createdResponse, HttpStatus.CREATED);
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<Response>> getAllResponses() {
        List<Response> responses = responseService.getAllResponses();
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<Response> getResponseById(@PathVariable Long id) {
        Response response = responseService.getResponseById(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteResponse(@PathVariable Long id) {
        responseService.deleteResponse(id);
        return new ResponseEntity<>("Response deleted successfully", HttpStatus.OK);
    }
}
