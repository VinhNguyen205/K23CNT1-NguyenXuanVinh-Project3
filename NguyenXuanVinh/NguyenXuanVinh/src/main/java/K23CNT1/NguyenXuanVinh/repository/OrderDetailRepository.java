package K23CNT1.NguyenXuanVinh.repository;

import K23CNT1.NguyenXuanVinh.entity.Order;
import K23CNT1.NguyenXuanVinh.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> {

    // Lấy list sản phẩm trong 1 đơn hàng
    List<OrderDetail> findByOrder(Order order);
}