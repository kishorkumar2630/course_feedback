package com.example.Service;

import com.example.Model.Student;
import com.example.Repo.StudentRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class StudentServiceImpl implements StudentService {

    @Autowired
    private StudentRepo studentRepo;

    @Override
    public Student createStudent(Student student) {
        return studentRepo.save(student);
    }

    @Override
    public List<Student> getAllStudents() {
        return studentRepo.findAll();
    }

    @Override
    public Student getStudentById(Long id) {
        return studentRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found with id: " + id));
    }

    @Override
    public Student updateStudent(Long id, Student studentDetails) {
        Student existingStudent = getStudentById(id);
        existingStudent.setStudentIdentifier(studentDetails.getStudentIdentifier());
        existingStudent.setName(studentDetails.getName());
        return studentRepo.save(existingStudent);
    }

    @Override
    public void deleteStudent(Long id) {
        Student existingStudent = getStudentById(id);
        studentRepo.delete(existingStudent);
    }
}
