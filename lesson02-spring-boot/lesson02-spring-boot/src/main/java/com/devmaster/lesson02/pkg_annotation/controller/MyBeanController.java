package com.devmaster.lesson02.pkg_annotation.controller;

import com.devmaster.lesson02.pkg_annotation.AppConfig; // Import lớp Config
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MyBeanController {

    private final AppConfig appConfig;

    // Tiêm (Inject) chính lớp Configuration
    @Autowired
    public MyBeanController(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    @GetMapping("/my-bean")
    public String myBean() {
        // Gọi phương thức @Bean để lấy Bean và trả về
        return appConfig.appName();
    }
}