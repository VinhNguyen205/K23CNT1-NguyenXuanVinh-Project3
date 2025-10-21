package com.devmaster.lesson02.pkg_annotation.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MyParamController {

    /**
     * Sử dụng @RequestParam để lấy query parameter
     * VD: /my-param?name=Vinh
     */
    @GetMapping("/my-param")
    public String searchUsers(@RequestParam(value = "name", required = false) String name) {
        if (name == null) {
            // Trường hợp 1: /my-param
            return "No name provided, returning all users";
        } else {
            // Trường hợp 2: /my-param?name=Chung Trinh
            return "<h1>Searching for users with name: " + name + "</h1>";
        }
    }

    /**
     * Sử dụng @PathVariable để lấy giá trị từ đường dẫn
     * VD: /my-variable/123
     */
    @GetMapping("/my-variable/{id}")
    public String getUserById(@PathVariable String id) {
        // (Trong hình, thuộc tính 'required = false' được thêm vào
        // @PathVariable, nhưng nó không phổ biến bằng với @RequestParam.
        // Dùng @PathVariable String id là đủ.)

        // Trường hợp: /my-variable/2209
        return "User ID is " + id;
    }
}