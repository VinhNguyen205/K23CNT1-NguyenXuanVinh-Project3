package com.devmaster.lesson02.pkg_annotation.service;

import org.springframework.stereotype.Service;

@Service // Đánh dấu đây là một Service Bean
public class MyGreetingService {
    public String greet() {
        return "<h1>Hello from MyGreetingService!</h1>";
    }
}