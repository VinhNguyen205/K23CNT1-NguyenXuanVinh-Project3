package K23CNT1.NguyenXuanVinh.dto;
import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String password;
    private String fullName;
    private String email;
}