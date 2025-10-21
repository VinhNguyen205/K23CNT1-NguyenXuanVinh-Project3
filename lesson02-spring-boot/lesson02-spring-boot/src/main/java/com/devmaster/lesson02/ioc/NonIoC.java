package com.devmaster.lesson02.ioc;

// Dịch vụ
class Service {
    public void serve() {
        System.out.println("Service is serving");
    }
}

// Máy khách (bị ràng buộc chặt)
class Client {
    // === RÀNG BUỘC CHẶT (Tight Coupling) ===
    // Client tự tạo đối tượng Service mà nó phụ thuộc vào
    private Service service = new Service();

    public void doSomething() {
        service.serve();
    }
}

// Lớp để chạy
public class NonIoC {
    public static void main(String[] args) {
        Client client = new Client();
        client.doSomething();
    }
}