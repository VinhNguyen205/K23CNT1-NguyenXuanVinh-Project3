package com.devmaster.lesson01.method_ref;

class MathUtils {
    public MathUtils() {}
    public MathUtils(String str) {
        System.out.println("MathUtils: " + str);
    }

    // 1. Phương thức static
    public static int sum(int a, int b) {
        return a + b;
    }

    // 2. Phương thức static
    public static int minus(int a, int b) {
        return a - b;
    }

    // 3. Phương thức instance (không static)
    public int multiply(int a, int b) {
        return a * b;
    }
}