package K23CNT1.NguyenXuanVinh.service.impl;

import K23CNT1.NguyenXuanVinh.entity.*;
import K23CNT1.NguyenXuanVinh.repository.*;
import K23CNT1.NguyenXuanVinh.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired private CartRepository cartRepository;
    @Autowired private CartItemRepository cartItemRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private BlindBoxRepository blindBoxRepository;

    @Override
    @Transactional
    public void addBoxToCart(Integer userId, Integer boxId, int quantity) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        BlindBox box = blindBoxRepository.findById(boxId).orElseThrow(() -> new RuntimeException("Box not found"));

        // 1. Tìm giỏ hàng hiện tại, nếu chưa có thì tạo mới
        Cart cart = cartRepository.findByUser(user).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUser(user);
            newCart.setCreatedAt(LocalDateTime.now());
            return cartRepository.save(newCart);
        });

        // 2. Kiểm tra xem Hộp này đã có trong giỏ chưa
        // (Lưu ý: Chúng ta dùng BoxID để check, ProductID để null vì đang mua Box)
        // Bạn cần tự viết hàm findByCartAndBlindBox trong CartItemRepository nếu chưa có, hoặc dùng logic dưới đây:

        Optional<CartItem> existingItem = cartItemRepository.findByCart(cart).stream()
                .filter(item -> item.getBlindBox() != null && item.getBlindBox().getBoxId().equals(boxId))
                .findFirst();

        if (existingItem.isPresent()) {
            // Có rồi -> Tăng số lượng
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
            cartItemRepository.save(item);
        } else {
            // Chưa có -> Tạo dòng mới
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setBlindBox(box);
            newItem.setQuantity(quantity);
            newItem.setAddedAt(LocalDateTime.now());
            cartItemRepository.save(newItem);
        }
    }

    @Override
    public Cart getCart(Integer userId) {
        User user = userRepository.findById(userId).orElse(null);
        return cartRepository.findByUser(user).orElse(null);
    }

    @Override
    @Transactional
    public void removeCartItem(Integer cartItemId) {
        cartItemRepository.deleteById(cartItemId);
    }

    @Override
    @Transactional
    public void clearCart(Integer userId) {
        User user = userRepository.findById(userId).orElse(null);
        cartRepository.findByUser(user).ifPresent(cart -> cartItemRepository.deleteByCart(cart));
    }
}