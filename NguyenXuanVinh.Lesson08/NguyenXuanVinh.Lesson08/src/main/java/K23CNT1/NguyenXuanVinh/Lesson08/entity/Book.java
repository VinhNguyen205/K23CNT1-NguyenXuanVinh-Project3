package K23CNT1.NguyenXuanVinh.Lesson08.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "book")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Sửa lại IDENTITY cho chuẩn MySQL
    private Long id;

    private String code;
    private String name;
    private String description;
    private String imgUrl;
    private Integer quantity;
    private Double price;
    private Boolean isActive;

    // --- ĐOẠN NÀY THAY ĐỔI ---
    // Thay vì @ManyToMany trực tiếp, ta dùng OneToMany sang bảng trung gian
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookAuthor> bookAuthors = new ArrayList<>();
}