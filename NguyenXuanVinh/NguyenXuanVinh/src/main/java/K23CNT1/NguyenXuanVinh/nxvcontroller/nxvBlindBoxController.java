package K23CNT1.NguyenXuanVinh.nxvcontroller;

import K23CNT1.NguyenXuanVinh.nxvdto.nxvOpenBoxResponse; // Đã đổi tên
import K23CNT1.NguyenXuanVinh.nxvdto.nxvSellItemResponse; // Đã đổi tên
import K23CNT1.NguyenXuanVinh.nxvservice.nxvBlindBoxService; // Đã đổi tên
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/blindbox")
public class nxvBlindBoxController { // Đã đổi tên Class

    @Autowired
    private nxvBlindBoxService nxvBlindBoxService; // Đã đổi tên type và variable

    // API 1: Mua và Mở hộp
    // URL: POST http://localhost:8080/api/blindbox/open?userId=1&boxId=1
    @PostMapping("/open")
    public ResponseEntity<?> openBox(@RequestParam Integer userId, @RequestParam Integer boxId) {
        try {
            // Đã gọi qua service mới và nhận response DTO mới
            nxvOpenBoxResponse result = nxvBlindBoxService.buyAndOpenBox(userId, boxId);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // API 2: Bán lại vật phẩm (Buyback)
    // URL: POST http://localhost:8080/api/blindbox/sell?userId=1&inventoryId=1
    @PostMapping("/sell")
    public ResponseEntity<?> sellItem(@RequestParam Integer userId, @RequestParam Integer inventoryId) {
        try {
            // Đã gọi qua service mới và nhận response DTO mới
            nxvSellItemResponse result = nxvBlindBoxService.sellItem(userId, inventoryId);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}