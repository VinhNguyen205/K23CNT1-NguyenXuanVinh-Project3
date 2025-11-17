package K23CNT1.NguyenXuanVinhLab07.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "books") // Tên bảng trong database
@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String name;
    String imgUrl;
    Integer quantity; // Qty
    Double price;      // Price
    String yearRelease;
    String author;
    Boolean status;

    // Mối quan hệ: Nhiều Book thuộc về 1 Category
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    Category category;
}