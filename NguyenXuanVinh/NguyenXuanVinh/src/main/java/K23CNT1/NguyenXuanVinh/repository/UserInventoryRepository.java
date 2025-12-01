package K23CNT1.NguyenXuanVinh.repository;

import K23CNT1.NguyenXuanVinh.entity.User;
import K23CNT1.NguyenXuanVinh.entity.UserInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserInventoryRepository extends JpaRepository<UserInventory, Integer> {

    // 1. Lấy toàn bộ kho đồ của User (Để hiện trang "Tủ đồ của tôi")
    List<UserInventory> findByUser(User user);

    // 2. Lấy kho đồ nhưng lọc theo trạng thái
    // Ví dụ: findByUserAndStatus(user, "IN_STORAGE") -> Chỉ lấy món đang nằm trong kho chờ xử lý
    List<UserInventory> findByUserAndStatus(User user, String status);

    // 3. Tìm món đồ cụ thể của user (Để check xem user có sở hữu món này thật không trước khi cho Bán/Ship)
    Optional<UserInventory> findByInventoryIdAndUser(Integer inventoryId, User user);
}