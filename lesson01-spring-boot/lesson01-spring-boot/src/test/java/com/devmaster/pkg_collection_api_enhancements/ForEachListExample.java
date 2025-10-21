package com.devmaster.pkg_collection_api_enhancements;

import java.util.Arrays;
import java.util.List;

public class ForEachListExample {
    public static void main(String[] args) {
        List<String> languages = Arrays.asList("Java Spring", "C#", "NetCore API", "PHP Laravel", "Javascript");

        // 1. Sử dụng biểu thức Lambda
        System.out.println("Sử dụng biểu thức Lambda:");
        languages.forEach(lang -> System.out.println(lang));

        // 2. Sử dụng method reference (ngắn gọn hơn)
        System.out.println("\nSử dụng method reference:");
        languages.forEach(System.out::println);
    }
}