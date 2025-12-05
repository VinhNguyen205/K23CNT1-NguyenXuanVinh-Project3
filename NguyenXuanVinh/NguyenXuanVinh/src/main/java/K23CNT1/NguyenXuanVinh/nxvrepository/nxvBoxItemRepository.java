package K23CNT1.NguyenXuanVinh.nxvrepository;

import K23CNT1.NguyenXuanVinh.nxventity.nxvBlindBox;
import K23CNT1.NguyenXuanVinh.nxventity.nxvBoxItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface nxvBoxItemRepository extends JpaRepository<nxvBoxItem, Integer> {
    // Tìm item trong 1 hộp cụ thể
    List<nxvBoxItem> findByBlindBox(nxvBlindBox blindBox);
}