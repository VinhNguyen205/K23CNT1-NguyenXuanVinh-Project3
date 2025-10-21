package com.devmaster.lesson01.pkg_stream_api;

import java.util.Arrays;
import java.util.List;

public class StreamExample {

    // Danh sách dữ liệu nguồn
    List<Integer> integerList = Arrays.asList(1, 22, 33, 35, 46, 66);

    /**
     * Đếm các số chẵn - Không dùng stream
     */
    public void withoutStream() {
        int count = 0;
        for (Integer integer : integerList) {
            if (integer % 2 == 0) { // Kiểm tra chẵn
                count++;
            }
        }
        System.out.println("WithoutStream -> Số phần tử chẵn: " + count);
    }

    /**
     * Đếm các số chẵn - Dùng Stream
     */
    public void withStream() {
        // Tạo stream -> lọc (filter) -> đếm (count)
        long count = integerList.stream()
                .filter(num -> num % 2 == 0)
                .count();

        System.out.println("WithStream    -> Số phần tử chẵn: " + count);
    }

    /**
     * Phương thức main để chạy thử
     */
    public static void main(String[] args) {
        StreamExample streamExample = new StreamExample();

        streamExample.withoutStream(); // Gọi cách 1
        streamExample.withStream();    // Gọi cách 2
    }
}