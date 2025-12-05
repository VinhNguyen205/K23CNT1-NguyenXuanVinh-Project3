package K23CNT1.NguyenXuanVinh.nxvrepository;
import K23CNT1.NguyenXuanVinh.nxventity.nxvUser;
import K23CNT1.NguyenXuanVinh.nxventity.nxvUserInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface nxvUserInventoryRepository extends JpaRepository<nxvUserInventory, Integer> {
    List<nxvUserInventory> findByUserAndStatus(nxvUser user, String status);
    Optional<nxvUserInventory> findByInventoryIdAndUser(Integer inventoryId, nxvUser user);
}