package K23CNT1.NguyenXuanVinh.repository;

import K23CNT1.NguyenXuanVinh.entity.Cart;
import K23CNT1.NguyenXuanVinh.entity.CartItem;
import K23CNT1.NguyenXuanVinh.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Integer> {

    // Lấy tất cả món trong 1 giỏ hàng
    List<CartItem> findByCart(Cart cart);

    // Kiểm tra xem sản phẩm này đã có trong giỏ chưa (để cộng dồn số lượng)
    Optional<CartItem> findByCartAndProduct(Cart cart, Product product);

    // Xóa sạch giỏ hàng sau khi thanh toán
    void deleteByCart(Cart cart);
}