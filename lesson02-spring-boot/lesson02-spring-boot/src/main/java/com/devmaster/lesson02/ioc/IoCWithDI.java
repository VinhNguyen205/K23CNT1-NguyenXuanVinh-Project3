package com.devmaster.lesson02.ioc;

// Dịch vụ
class IocService {
    public void serve() {
        System.out.println("Service is serving");
    }
}

// Máy khách (đã được nới lỏng)
class IocClient {
    // Chỉ khai báo, không khởi tạo
    private IocService locService;

    // === DEPENDENCY INJECTION ===
    // Dùng DI để "tiêm" service vào qua constructor
    public IocClient(IocService service) {
        this.locService = service;
    }

    public void doSomething() {
        locService.serve();
    }
}

// Lớp để chạy (Nơi "lắp ráp" các phụ thuộc)
public class IoCWithDI {
    public static void main(String[] args) {
        // === Inversion of Control ===
        // Quyền tạo Service giờ nằm ở đây (bên ngoài Client)

        // 1. Tạo đối tượng Service
        IocService service = new IocService();

        // 2. "Tiêm" (Inject) Service vào Client khi tạo Client
        IocClient client = new IocClient(service);

        // 3. Chạy
        client.doSomething();
    }
}