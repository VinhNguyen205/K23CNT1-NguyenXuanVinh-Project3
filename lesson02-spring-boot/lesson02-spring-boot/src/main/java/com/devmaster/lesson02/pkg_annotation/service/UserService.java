package com.devmaster.lesson02.pkg_annotation.service;

import org.springframework.stereotype.Service;

@Service // Đánh dấu đây là 1 Service Bean
public class UserService {

    public String getUserDetails() {
        return "<h1>User details</h1>";
    }
}