package K23CNT1.NguyenXuanVinh.Lesson08.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String code;
    private String name;
    private String description;
    private String imgUrl;
    private String email;
    private String phone;
    private String address;
    private Boolean isActive;

    /**
     * Tạo mối quan hệ với bảng book (Many-to-Many)
     * mappedBy = "authors": Chỉ ra rằng mối quan hệ này
     * đã được định nghĩa bởi trường 'authors' ở bên class Book.
     * Bên này sẽ không tạo ra bảng trung gian.
     */
    @ManyToMany(mappedBy = "authors")
    private List<Book> books = new ArrayList<>();
}