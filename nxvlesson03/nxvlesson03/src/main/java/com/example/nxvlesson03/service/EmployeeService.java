package com.example.nxvlesson03.service;

import com.example.nxvlesson03.entity.Employee;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class EmployeeService {
    private List<Employee> listEmployee = new ArrayList<>();

    public EmployeeService() {
        listEmployee.addAll(Arrays.asList(
                new Employee(1L, "Trần Văn An", "Nam", 25, 15000000),
                new Employee(2L, "Lê Thị Bình", "Nữ", 30, 20000000),
                new Employee(3L, "Nguyễn Hữu Cảnh", "Nam", 22, 12000000),
                new Employee(4L, "Phạm Thị Dung", "Nữ", 28, 18000000),
                new Employee(5L, "Đỗ Văn Em", "Nam", 35, 25000000)
        ));
    }

    // Lấy toàn bộ danh sách
    public List<Employee> getAllEmployees() {
        return listEmployee;
    }

    // Lấy theo id
    public Employee getEmployeeById(Long id) {
        return listEmployee.stream()
                .filter(emp -> emp.getId().equals(id))
                .findFirst().orElse(null);
    }

    // Thêm mới
    public Employee addEmployee(Employee employee) {
        // Tự động gán ID mới (đơn giản)
        Long maxId = listEmployee.stream()
                .mapToLong(Employee::getId)
                .max()
                .orElse(0L);
        employee.setId(maxId + 1);
        listEmployee.add(employee);
        return employee;
    }

    // Sửa đổi
    public Employee updateEmployee(Long id, Employee empDetails) {
        Employee employee = getEmployeeById(id);
        if (employee != null) {
            employee.setFullName(empDetails.getFullName());
            employee.setGender(empDetails.getGender());
            employee.setAge(empDetails.getAge());
            employee.setSalary(empDetails.getSalary());
            return employee;
        }
        return null;
    }

    // Xóa
    public boolean deleteEmployee(Long id) {
        Employee employee = getEmployeeById(id);
        if (employee != null) {
            return listEmployee.remove(employee);
        }
        return false;
    }
}