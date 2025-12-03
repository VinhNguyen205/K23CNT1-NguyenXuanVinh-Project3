package K23CNT1.NguyenXuanVinh.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder // Pattern Builder giúp tạo object cực ngầu
public class DashboardStats {
    private long totalUsers;
    private BigDecimal totalDeposit;
    private BigDecimal totalSpent;
}