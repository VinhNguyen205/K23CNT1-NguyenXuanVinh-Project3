package K23CNT1.NguyenXuanVinhLab07.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "products")
@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String name;
    String imageUrl;
    Integer quantity;
    Double price;
    String content;

    // --- TRƯỜNG MỚI ĐƯỢC THÊM ---
    Boolean status; // Dùng cho checkbox 'status'

    // --- MỐI QUAN HỆ MỚI ĐƯỢC THÊM ---
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    Category category; // Dùng cho dropdown 'category'
}