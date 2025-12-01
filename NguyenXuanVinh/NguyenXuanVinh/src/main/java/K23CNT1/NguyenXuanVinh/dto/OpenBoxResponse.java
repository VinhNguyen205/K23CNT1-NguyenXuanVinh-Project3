package K23CNT1.NguyenXuanVinh.dto;

import K23CNT1.NguyenXuanVinh.entity.UserInventory;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class OpenBoxResponse {
    private String itemName;      // Tên món trúng
    private String itemImage;     // Ảnh món trúng
    private String rarity;        // Độ hiếm (S, A, B...)
    private BigDecimal currentBalance; // Số dư còn lại sau khi mua
    private boolean isPityTriggered;   // Có phải trúng do bảo hiểm không?
    private UserInventory inventoryItem; // Trả về cả cục inventory để tiện xử lý sau này
}