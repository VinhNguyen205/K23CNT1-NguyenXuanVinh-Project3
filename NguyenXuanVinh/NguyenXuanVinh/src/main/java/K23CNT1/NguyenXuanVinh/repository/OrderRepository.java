package K23CNT1.NguyenXuanVinh.repository;

import K23CNT1.NguyenXuanVinh.entity.Order;
import K23CNT1.NguyenXuanVinh.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    // Lịch sử mua hàng của user (Mới nhất lên đầu)
    List<Order> findByUserOrderByOrderDateDesc(User user);

    // (Admin) Lọc đơn hàng theo trạng thái (VD: PENDING, SHIPPING)
    List<Order> findByOrderStatus(String status);
}