package K23CNT1.NguyenXuanVinh.controller;

import K23CNT1.NguyenXuanVinh.dto.OpenBoxResponse;
import K23CNT1.NguyenXuanVinh.dto.SellItemResponse;
import K23CNT1.NguyenXuanVinh.service.BlindBoxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/blindbox")
public class BlindBoxController {

    @Autowired
    private BlindBoxService blindBoxService;

    // API 1: Mua và Mở hộp
    // URL: POST http://localhost:8080/api/blindbox/open?userId=1&boxId=1
    @PostMapping("/open")
    public ResponseEntity<?> openBox(@RequestParam Integer userId, @RequestParam Integer boxId) {
        try {
            OpenBoxResponse result = blindBoxService.buyAndOpenBox(userId, boxId);
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
            SellItemResponse result = blindBoxService.sellItem(userId, inventoryId);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}