package K23CNT1.NguyenXuanVinh.repository;

import K23CNT1.NguyenXuanVinh.entity.ShipmentDetail;
import K23CNT1.NguyenXuanVinh.entity.ShipmentRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShipmentDetailRepository extends JpaRepository<ShipmentDetail, Integer> {

    // Lấy danh sách các món đồ nằm trong 1 đơn ship cụ thể
    List<ShipmentDetail> findByShipmentRequest(ShipmentRequest shipmentRequest);
}