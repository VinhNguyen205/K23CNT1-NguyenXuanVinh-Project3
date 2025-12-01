package K23CNT1.NguyenXuanVinh.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "ShipmentDetails")
public class ShipmentDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DetailID")
    private Integer detailId;

    @ManyToOne
    @JoinColumn(name = "ShipmentID")
    private ShipmentRequest shipmentRequest;

    // Link tới vật phẩm cụ thể trong kho
    @ManyToOne
    @JoinColumn(name = "InventoryID")
    private UserInventory userInventory;
}