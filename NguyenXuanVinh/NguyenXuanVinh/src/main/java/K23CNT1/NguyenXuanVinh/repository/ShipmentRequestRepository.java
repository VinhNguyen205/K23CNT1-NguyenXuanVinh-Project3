package K23CNT1.NguyenXuanVinh.repository;

import K23CNT1.NguyenXuanVinh.entity.ShipmentRequest;
import K23CNT1.NguyenXuanVinh.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShipmentRequestRepository extends JpaRepository<ShipmentRequest, Integer> {

    // Xem lịch sử đơn hàng của bản thân user
    List<ShipmentRequest> findByUserOrderByRequestDateDesc(User user);

    // (Dành cho Admin) Lọc các đơn hàng theo trạng thái (VD: Lấy list "PENDING" để đi đóng hàng)
    List<ShipmentRequest> findByShipmentStatus(String shipmentStatus);
}