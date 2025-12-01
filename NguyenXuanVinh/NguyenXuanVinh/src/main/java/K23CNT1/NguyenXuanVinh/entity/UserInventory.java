package K23CNT1.NguyenXuanVinh.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "UserInventory")
public class UserInventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "InventoryID")
    private Integer inventoryId;

    @ManyToOne
    @JoinColumn(name = "UserID")
    private User user;

    @ManyToOne
    @JoinColumn(name = "ItemID")
    private BoxItem boxItem;

    @Column(name = "ObtainedDate")
    private LocalDateTime obtainedDate;

    @Column(name = "Status", length = 50)
    private String status; // IN_STORAGE, SOLD_BACK...

    @Column(name = "SoldPrice", precision = 18, scale = 2)
    private BigDecimal soldPrice;

    @PrePersist
    protected void onCreate() {
        obtainedDate = LocalDateTime.now();
        if (status == null) status = "IN_STORAGE";
    }
}