package K23CNT1.NguyenXuanVinh.controller;

import K23CNT1.NguyenXuanVinh.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/order")
public class OrderApiController {

    @Autowired private OrderService orderService;

    // API Thanh toán
    // POST /api/order/checkout
    @PostMapping("/checkout")
    public ResponseEntity<?> checkout(@RequestParam Integer userId,
                                      @RequestParam String name,
                                      @RequestParam String phone,
                                      @RequestParam String address) {
        try {
            orderService.placeOrder(userId, name, phone, address);
            return ResponseEntity.ok("Đặt hàng thành công! Mã đơn hàng đã được tạo.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}