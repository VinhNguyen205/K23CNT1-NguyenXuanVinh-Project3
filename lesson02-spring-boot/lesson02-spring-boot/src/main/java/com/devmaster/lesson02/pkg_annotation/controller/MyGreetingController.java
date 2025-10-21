package com.devmaster.lesson02.pkg_annotation.controller;

import com.devmaster.lesson02.pkg_annotation.service.MyGreetingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MyGreetingController {

    // 1. Khai báo phụ thuộc
    private final MyGreetingService myGreetingService;

    // 2. Tiêm phụ thuộc (Inject) qua constructor
    @Autowired
    public MyGreetingController(MyGreetingService myGreetingService) {
        this.myGreetingService = myGreetingService;
    }

    // 3. Tạo endpoint
    @GetMapping("/my-greet")
    public String greet() {
        // 4. Gọi service đã được tiêm
        return myGreetingService.greet();
    }
}