package com.devmaster.lesson02.pkg_annotation;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration // Đánh dấu đây là lớp Cấu hình
public class AppConfig {

    @Bean // Đánh dấu phương thức này tạo ra 1 Bean
    public String appName() {
        return "<h1>VIỆN CÔNG NGHỆ IDK</h1>" +
                "<h2>Spring Boot Application</h2>";
    }
}