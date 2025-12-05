package K23CNT1.NguyenXuanVinh.nxvdto;

import K23CNT1.NguyenXuanVinh.nxventity.nxvUserInventory; // Đã đổi tên Entity
import lombok.Data;
import java.math.BigDecimal;

@Data
public class nxvOpenBoxResponse { // Đã đổi tên Class
    private String itemName;
    private String itemImage;
    private String rarity;
    private BigDecimal currentBalance;
    private boolean isPityTriggered;
    private nxvUserInventory inventoryItem; // Đã đổi tên Type
}