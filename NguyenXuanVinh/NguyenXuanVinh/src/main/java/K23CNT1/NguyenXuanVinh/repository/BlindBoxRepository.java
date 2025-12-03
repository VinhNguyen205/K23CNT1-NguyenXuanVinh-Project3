package K23CNT1.NguyenXuanVinh.repository;

import K23CNT1.NguyenXuanVinh.entity.BlindBox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BlindBoxRepository extends JpaRepository<BlindBox, Integer> {

    List<BlindBox> findByBoxNameContaining(String boxName);
}