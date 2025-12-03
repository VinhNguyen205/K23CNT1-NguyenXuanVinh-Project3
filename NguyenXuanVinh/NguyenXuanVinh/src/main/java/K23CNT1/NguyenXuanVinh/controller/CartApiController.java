package K23CNT1.NguyenXuanVinh.controller;

import K23CNT1.NguyenXuanVinh.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartApiController {

    @Autowired private ShoppingCartService cartService;

    // Thêm vào giỏ
    @PostMapping("/add")
    public ResponseEntity<?> addToCart(@RequestParam Integer userId, @RequestParam Integer boxId) {
        try {
            cartService.addBoxToCart(userId, boxId, 1); // Mặc định thêm 1
            return ResponseEntity.ok("Đã thêm vào giỏ hàng!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Xóa khỏi giỏ
    @DeleteMapping("/remove/{itemId}")
    public ResponseEntity<?> removeFromCart(@PathVariable Integer itemId) {
        cartService.removeCartItem(itemId);
        return ResponseEntity.ok("Đã xóa!");
    }
}