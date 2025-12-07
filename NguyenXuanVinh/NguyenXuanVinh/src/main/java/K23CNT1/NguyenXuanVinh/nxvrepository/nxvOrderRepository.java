package K23CNT1.NguyenXuanVinh.nxvrepository;

import K23CNT1.NguyenXuanVinh.nxventity.nxvOrder;
import K23CNT1.NguyenXuanVinh.nxventity.nxvUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface nxvOrderRepository extends JpaRepository<nxvOrder, Integer> {
    // Lịch sử mua hàng (Mới nhất lên đầu)
    List<nxvOrder> findByUserOrderByOrderDateDesc(nxvUser user);
    List<nxvOrder> findAllByOrderByOrderDateDesc();
}