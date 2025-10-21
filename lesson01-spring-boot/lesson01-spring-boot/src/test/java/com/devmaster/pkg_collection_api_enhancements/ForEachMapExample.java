package com.devmaster.pkg_collection_api_enhancements;

import java.util.HashMap;
import java.util.Map;

public class ForEachMapExample {
    public static void main(String[] args) {
        Map<Integer, String> hmap = new HashMap<>();

        // Thêm dữ liệu vào Map
        hmap.put(1, "Java Spring");
        hmap.put(2, "Javascript");
        hmap.put(3, "PHP Laravel");
        hmap.put(4, "C# NetCore"); // Trong hình là "C# NetCore)"

        // Hiển thị dữ liệu
        System.out.println("Hiển thị dữ liệu từ Map:");
        hmap.forEach((key, value) -> System.out.println(key + " - " + value));
    }
}