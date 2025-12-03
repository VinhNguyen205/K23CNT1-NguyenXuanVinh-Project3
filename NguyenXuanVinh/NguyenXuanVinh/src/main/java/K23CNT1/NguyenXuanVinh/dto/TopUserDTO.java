package K23CNT1.NguyenXuanVinh.dto;

import K23CNT1.NguyenXuanVinh.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor // Bắt buộc có để dùng trong câu Query JPQL
public class TopUserDTO {
    private User user;
    private BigDecimal totalAmount;
}