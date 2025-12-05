package K23CNT1.NguyenXuanVinh.nxvrepository;

import K23CNT1.NguyenXuanVinh.nxventity.nxvShipmentRequest;
import K23CNT1.NguyenXuanVinh.nxventity.nxvUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface nxvShipmentRequestRepository extends JpaRepository<nxvShipmentRequest, Integer> {
    // Chú ý: Entity dùng nxvUser nên ở đây phải gọi đúng tên trường map
    // Trong Entity nxvShipmentRequest: private nxvUser nxvUser;
    // -> Method phải là findByNxvUser...
    List<nxvShipmentRequest> findByNxvUserOrderByRequestDateDesc(nxvUser nxvUser);
}