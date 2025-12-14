package K23CNT1.NguyenXuanVinh.nxvrepository;

import K23CNT1.NguyenXuanVinh.nxventity.nxvShipmentRequest;
import K23CNT1.NguyenXuanVinh.nxventity.nxvUser;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface nxvShipmentRequestRepository extends JpaRepository<nxvShipmentRequest, Integer> {
    // Lấy lịch sử yêu cầu ship của user, mới nhất lên đầu
    List<nxvShipmentRequest> findByUserOrderByRequestDateDesc(nxvUser user);
}