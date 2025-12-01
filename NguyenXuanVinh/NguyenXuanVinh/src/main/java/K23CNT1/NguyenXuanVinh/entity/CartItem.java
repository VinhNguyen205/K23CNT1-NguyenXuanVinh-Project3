package K23CNT1.NguyenXuanVinh.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "CartItems")
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CartItemID")
    private Integer cartItemId;

    @ManyToOne
    @JoinColumn(name = "CartID")
    private Cart cart;

    // Link tới sản phẩm thường (có thể null)
    @ManyToOne
    @JoinColumn(name = "ProductID")
    private Product product;

    // Link tới Blind Box (có thể null) - Mua hộp chưa mở
    @ManyToOne
    @JoinColumn(name = "BoxID")
    private BlindBox blindBox;

    @Column(name = "Quantity")
    private Integer quantity;

    @Column(name = "AddedAt")
    private LocalDateTime addedAt;
}