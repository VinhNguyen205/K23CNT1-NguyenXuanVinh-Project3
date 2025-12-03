package K23CNT1.NguyenXuanVinh.service;

import K23CNT1.NguyenXuanVinh.entity.Cart;

public interface ShoppingCartService {
    // Thêm hộp vào giỏ
    void addBoxToCart(Integer userId, Integer boxId, int quantity);

    // Lấy giỏ hàng của user
    Cart getCart(Integer userId);

    // Xóa món khỏi giỏ
    void removeCartItem(Integer cartItemId);

    // Xóa sạch giỏ (khi thanh toán xong)
    void clearCart(Integer userId);
}