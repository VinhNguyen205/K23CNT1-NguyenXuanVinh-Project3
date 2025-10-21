package com.devmaster.lesson02.ioc_spring;

import org.springframework.stereotype.Service;

@Service // Đánh dấu cho Spring biết đây là 1 Bean (Service)
public class GreetingServiceImpl implements GreetingService {
    @Override
    public String greet(String name) {
        // Trả về một chuỗi HTML
        return "<h2>Devmaster[Spring Boot!] Xin chào,</h2>" +
                "<h1 style=\"color:red; text-align:center\">" +
                name + "</h1>";
    }
}