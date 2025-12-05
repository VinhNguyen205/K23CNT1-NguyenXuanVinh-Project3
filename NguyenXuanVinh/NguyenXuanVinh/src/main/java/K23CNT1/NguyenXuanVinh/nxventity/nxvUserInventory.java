package K23CNT1.NguyenXuanVinh.nxventity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "UserInventory")
public class nxvUserInventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "InventoryID")
    private Integer inventoryId;

    @ManyToOne
    @JoinColumn(name = "UserID")
    private nxvUser user;

    @ManyToOne
    @JoinColumn(name = "ItemID")
    private nxvBoxItem boxItem;

    @Column(name = "ObtainedDate")
    private LocalDateTime obtainedDate;

    @Column(name = "Status") // IN_STORAGE, SOLD_BACK...
    private String status;

    @Column(name = "SoldPrice", precision = 18, scale = 2)
    private BigDecimal soldPrice;

    @PrePersist
    protected void onCreate() {
        obtainedDate = LocalDateTime.now();
        if (status == null) status = "IN_STORAGE";
    }
}