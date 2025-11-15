package K23CNT1.NxvLesson04.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.LocalDate;

@Data
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDTO {

    @NotBlank(message = "Username cannot be blank")
    @Size(min = 5, max = 20, message = "Username must be between 5 and 20 characters")
    @Column(unique = true) // Lấy từ import trong hình
    String username;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 8, max = 30, message = "Password must be between 8 and 30 characters")
    @Pattern(regexp = "((?=.*[0-9])(?=.*[a-z]).{8,30})", message = "Password must contain at least one letter and one number")
    String password;

    @NotBlank(message = "Full name cannot be blank")
    @Size(min = 5, max = 50, message = "Full name must be between 5 and 50 characters")
    String fullName;

    @Past(message = "Birthday must be in the past")
    LocalDate birthDay;

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email cannot be blank")
    @Column(unique = true) // Lấy từ import trong hình
    String email;

    @Pattern(regexp = "(^\\+?[0-9]{10,12}$)", message = "Phone number is invalid")
    @NotBlank(message = "Phone number cannot be blank")
    String phone;

    @Min(value = 18, message = "Age must be at least 18")
    @Max(value = 100, message = "Age must be less than or equal to 100")
    int age;

    @NotNull(message = "Status cannot be null")
    Boolean status;
}