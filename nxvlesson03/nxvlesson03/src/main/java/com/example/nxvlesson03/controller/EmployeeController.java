package com.example.nxvlesson03.controller;

import com.example.nxvlesson03.entity.Employee;
import com.example.nxvlesson03.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    // API Lấy toàn bộ danh sách
    @GetMapping("/employee-list")
    public List<Employee> getAllEmployees() {
        return employeeService.getAllEmployees();
    }

    // API Lấy theo id
    @GetMapping("/employee/{id}")
    public Employee getEmployeeById(@PathVariable Long id) {
        // ID là Long, nên không cần parse
        return employeeService.getEmployeeById(id);
    }

    // API Thêm mới
    @PostMapping("/employee-add")
    public Employee addEmployee(@RequestBody Employee employee) {
        return employeeService.addEmployee(employee);
    }

    // API Sửa
    @PutMapping("/employee/{id}")
    public Employee updateEmployee(@PathVariable Long id, @RequestBody Employee employee) {
        return employeeService.updateEmployee(id, employee);
    }

    // API Xóa
    @DeleteMapping("/employee/{id}")
    public boolean deleteEmployee(@PathVariable Long id) {
        return employeeService.deleteEmployee(id);
    }
}