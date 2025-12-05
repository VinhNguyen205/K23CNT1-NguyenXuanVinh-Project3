package K23CNT1.NguyenXuanVinh.nxvdto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class nxvSellItemResponse { // Đã đổi tên Class
    private String itemName;
    private BigDecimal soldPrice;
    private BigDecimal newBalance;
    private String message;
}