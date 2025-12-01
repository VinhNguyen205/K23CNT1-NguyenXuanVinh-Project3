package K23CNT1.NguyenXuanVinh.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class SellItemResponse {
    private String itemName;       // Tên món vừa bán
    private BigDecimal soldPrice;  // Giá bán được (đã trừ 10%)
    private BigDecimal newBalance; // Số dư ví mới
    private String message;        // Thông báo (VD: Bán thành công!)
}