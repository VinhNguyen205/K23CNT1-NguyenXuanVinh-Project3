package com.devmaster.lesson02.pkg_annotation.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("hello") // Map đường dẫn /hello với phương thức này
    public String sayHello() {
        return "Hello, Spring Boot!";
    }
}