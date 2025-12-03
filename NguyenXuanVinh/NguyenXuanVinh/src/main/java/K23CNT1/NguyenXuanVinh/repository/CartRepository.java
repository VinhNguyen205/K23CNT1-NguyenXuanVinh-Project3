package K23CNT1.NguyenXuanVinh.repository;

import K23CNT1.NguyenXuanVinh.entity.Cart;
import K23CNT1.NguyenXuanVinh.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Integer> {
    // Tìm giỏ hàng hiện tại của user
    Optional<Cart> findByUser(User user);
}