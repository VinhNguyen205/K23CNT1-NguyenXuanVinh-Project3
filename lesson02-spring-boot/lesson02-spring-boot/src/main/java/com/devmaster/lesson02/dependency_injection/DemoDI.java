package com.devmaster.lesson02.dependency_injection;

public class DemoDI {
    public static void main(String[] args) {
        // 1. Quyết định dùng CircleShape
        Shape circle = new CircleShape();

        // 2. Tiêm (Inject) CircleShape vào DrawShape
        DrawShape drawShape = new DrawShape(circle);

        // 3. Chạy -> Sẽ in ra "CircleShape draw"
        drawShape.draw();

        // --- Thay đổi dễ dàng ---

        // 1. Quyết định dùng RectangleShape
        Shape rectangle = new RectangleShape();

        // 2. Tiêm (Inject) RectangleShape vào cùng một biến drawShape
        drawShape = new DrawShape(rectangle);

        // 3. Chạy -> Sẽ in ra "RectangleShape draw"
        drawShape.draw();
    }
}