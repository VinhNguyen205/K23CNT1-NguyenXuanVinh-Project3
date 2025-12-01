package K23CNT1.NguyenXuanVinh.repository;

import K23CNT1.NguyenXuanVinh.entity.BoxItem;
import K23CNT1.NguyenXuanVinh.entity.BlindBox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BoxItemRepository extends JpaRepository<BoxItem, Integer> {
    // Lấy tất cả món đồ trong 1 cái hộp cụ thể
    List<BoxItem> findByBlindBox(BlindBox blindBox);
}