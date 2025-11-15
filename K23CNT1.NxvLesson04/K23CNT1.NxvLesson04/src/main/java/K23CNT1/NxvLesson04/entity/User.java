package K23CNT1.NxvLesson04.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor // Cần AllArgsConstructor để code Service chạy
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "Users")
@Getter // (Đã bao gồm trong @Data)
@Setter // (Đã bao gồm trong @Data)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    String username;
    String password;
    String fullName;

    // --- BỔ SUNG CÁC TRƯỜNG BỊ THIẾU ---
    LocalDate birthDay;
    String email;
    String phone;
    int age;
    Boolean status;
}