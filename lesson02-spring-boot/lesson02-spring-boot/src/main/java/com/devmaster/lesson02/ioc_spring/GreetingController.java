package com.devmaster.lesson02.ioc_spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController // Đánh dấu đây là 1 Controller
public class GreetingController {

    // 1. Chỉ khai báo phụ thuộc qua Interface
    private final GreetingService greetingService;

    // 2. Dùng @Autowired trên constructor để Spring tự động tiêm
    @Autowired
    public GreetingController(GreetingService greetingService) {
        this.greetingService = greetingService;
    }

    // 3. Tạo 1 endpoint (đường dẫn) để truy cập
    @GetMapping("/greet")
    public String greet() {
        // Gọi đến service đã được tiêm
        return greetingService.greet("Vinh");
    }
}