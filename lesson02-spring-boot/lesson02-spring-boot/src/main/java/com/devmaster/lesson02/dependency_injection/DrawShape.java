package com.devmaster.lesson02.dependency_injection;

public class DrawShape {
    // 1. Phụ thuộc (dependency) chỉ là interface
    private Shape shape;

    // 2. Phụ thuộc được "tiêm" vào qua constructor
    public DrawShape(Shape shape) {
        this.shape = shape;
    }

    // 3. Sử dụng phụ thuộc
    public void draw() {
        shape.draw();
    }
}