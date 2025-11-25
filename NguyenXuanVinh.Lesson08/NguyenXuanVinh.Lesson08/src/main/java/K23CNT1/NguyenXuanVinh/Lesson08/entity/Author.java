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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;
    private String name;
    private String description;
    private String imgUrl;
    private String email;
    private String phone;
    private String address;
    private Boolean isActive;

    // --- ĐOẠN NÀY ĐÃ ĐƯỢC SỬA ---
    // Thay vì @ManyToMany cũ, ta chuyển sang @OneToMany trỏ về bảng trung gian
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookAuthor> bookAuthors = new ArrayList<>();
}