package K23CNT1.NguyenXuanVinh.repository;

import K23CNT1.NguyenXuanVinh.entity.BlindBox;
import K23CNT1.NguyenXuanVinh.entity.User;
import K23CNT1.NguyenXuanVinh.entity.UserPityStat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserPityStatRepository extends JpaRepository<UserPityStat, Integer> {

    // Tìm bảng theo dõi bảo hiểm của User đối với 1 cái Hộp cụ thể
    // (Vì mỗi hộp có bộ đếm bảo hiểm riêng)
    Optional<UserPityStat> findByUserAndBlindBox(User user, BlindBox blindBox);
}