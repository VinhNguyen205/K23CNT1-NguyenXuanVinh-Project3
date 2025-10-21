package com.devmaster.lesson02_spring_boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Thêm scanBasePackages để quét TẤT CẢ các package bạn đã tạo
@SpringBootApplication(scanBasePackages = {
        "com.devmaster.lesson02_spring_boot",           // 1. Package của chính nó
        "com.devmaster.lesson02.ioc_spring",          // 2. Package của GreetingController
        "com.devmaster.lesson02.pkg_annotation",
        "com.devmaster.lesson02.pkg_annotation.controller", // 3. Package của HelloController
        "com.devmaster.lesson02.pkg_annotation.service"    // 4. (DÒNG MỚI) Package của UserService
})
public class Lesson02SpringBootApplication {

    public static void main(String[] args) {
        SpringApplication.run(Lesson02SpringBootApplication.class, args);
    }

}