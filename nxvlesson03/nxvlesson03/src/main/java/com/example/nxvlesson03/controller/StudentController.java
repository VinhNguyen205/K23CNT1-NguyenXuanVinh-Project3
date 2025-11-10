package com.example.nxvlesson03.controller;

import com.example.nxvlesson03.entity.Student;
import com.example.nxvlesson03.service.ServiceStudent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class StudentController {
    @Autowired
    private ServiceStudent ServiceStudent;

    /**
     * API lấy danh sách tất cả sinh viên
     * @return List<Student>
     */
    @GetMapping("/student-list")
    public List<Student> getAllStudents() {
        return ServiceStudent.getStudents();
    }

    /**
     * API lấy thông tin sinh viên theo ID
     * @param id
     * @return Student
     */
    // LƯU Ý: Sửa tên hàm (trong PDF bị trùng tên là getAllStudents)
    @GetMapping("/student/{id}")
    public Student getStudentById(@PathVariable String id) {
        Long param = Long.parseLong(id);
        return ServiceStudent.getStudent(param);
    }

    /**
     * API thêm mới sinh viên
     * @param student
     * @return Student
     */
    @PostMapping("/student-add")
    public Student addStudent(@RequestBody Student student) {
        return ServiceStudent.addStudent(student);
    }

    /**
     * API cập nhật thông tin sinh viên
     * @param id
     * @param student
     * @return Student
     */
    @PutMapping("/student/{id}")
    public Student updateStudent(@PathVariable String id, @RequestBody Student student) {
        Long param = Long.parseLong(id);
        return ServiceStudent.updateStudent(param, student);
    }

    /**
     * API xóa sinh viên
     * @param id
     * @return boolean
     */
    @DeleteMapping("/student/{id}")
    public boolean deleteStudent(@PathVariable String id) {
        Long param = Long.parseLong(id);
        return ServiceStudent.deleteStudent(param);
    }
}