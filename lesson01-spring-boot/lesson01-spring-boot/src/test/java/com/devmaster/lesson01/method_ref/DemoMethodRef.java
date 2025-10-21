package com.devmaster.lesson01.method_ref;

import java.util.Arrays; // Import này dùng cho Arrays.sort

public class DemoMethodRef {
    public static void main(String[] args) {
        int a = 10;
        int b = 20;

        // 1. Tham chiếu đến static method (MathUtils::sum)
        int sum = doAction(a, b, MathUtils::sum);
        System.out.println(a + " + " + b + " = " + sum);

        // Tương tự, tham chiếu đến static method (MathUtils::minus)
        int minus = doAction(a, b, MathUtils::minus);
        System.out.println(a + " - " + b + " = " + minus);

        // 2. Tham chiếu đến instance method của một đối tượng CỤ THỂ
        MathUtils mathUtils = new MathUtils(); // Phải tạo đối tượng trước
        int multiply = doAction(a, b, mathUtils::multiply); // Dùng đối tượng đó
        System.out.println(a + " * " + b + " = " + multiply);

        // 3. Tham chiếu đến instance method của một đối tượng TÙY Ý
        String[] stringArray = {"Java", "C++", "PHP", "C#", "Javascript"};

        // (lambda: (s1, s2) -> s1.compareToIgnoreCase(s2))
        Arrays.sort(stringArray, String::compareToIgnoreCase);

        System.out.println("\n--- Mảng sau khi sắp xếp ---");
        for (String str : stringArray) {
            System.out.println(str);
        }
    }

    // Phương thức này nhận một "hàm" (func) làm tham số
    public static int doAction(int a, int b, ExecuteFunction func) {
        return func.execute(a, b);
    }
}