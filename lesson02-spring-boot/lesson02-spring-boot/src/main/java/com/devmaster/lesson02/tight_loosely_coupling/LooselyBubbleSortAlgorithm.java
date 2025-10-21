package com.devmaster.lesson02.tight_loosely_coupling;

import java.util.Arrays;

// Một triển khai cụ thể của hợp đồng
public class LooselyBubbleSortAlgorithm implements SortAlgorithm {
    @Override
    public void sort(int[] array) {
        System.out.println("Sorted using bubble sort algorithm");

        // Ghi chú: Code trong hình dùng Arrays.stream().sorted()
        // để in ra kết quả cho nhanh, chứ không phải code bubble sort.
        // Dưới đây là code in ra như trong hình:
        Arrays.stream(array).sorted().forEach(System.out::println);

        // Nếu muốn dùng code bubble sort thật, bạn có thể copy từ ví dụ
        // "TightCoupling" trước đó vào đây.
    }
}