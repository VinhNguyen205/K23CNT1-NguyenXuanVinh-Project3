package com.devmaster.lesson02.pkg_annotation.controller;

// Import 2 thư viện mới
import com.devmaster.lesson02.pkg_annotation.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    // === PHẦN MỚI ===
    @Autowired // Yêu cầu Spring tiêm UserService vào đây
    private UserService userService;

    // Endpoint mới để gọi service
    // Truy cập: GET http://localhost:8080/user
    @GetMapping("/user") // LƯU Ý: đường dẫn là "/user" (số ít)
    public String getDetails() {
        // Gọi đến service đã được tiêm
        return userService.getUserDetails();
    }
    // === KẾT THÚC PHẦN MỚI ===


    // 1. GET: Lấy danh sách tất cả user
    // Truy cập: GET http://localhost:8080/users
    @GetMapping("/users")
    public String getUsers() {
        return "<h1>Get all users</h1>";
    }

    // 2. POST: Tạo một user mới
    // Truy cập: POST http://localhost:8080/users
    @PostMapping("/users")
    public String createUser() {
        return "<h1>User created</h1>";
    }

    // 3. PUT: Cập nhật user theo ID
    // Truy cập: PUT http://localhost:8080/users/123
    @PutMapping("/users/{id}")
    public String updateUser(@PathVariable int id) {
        return "<h1>User with ID " + id + " updated</h1>";
    }

    // 4. DELETE: Xóa user theo ID
    // Truy cập: DELETE http://localhost:8080/users/456
    @DeleteMapping("/users/{id}")
    public String deleteUser(@PathVariable int id) {
        return "<h1>User with ID " + id + " deleted</h1>";
    }
}