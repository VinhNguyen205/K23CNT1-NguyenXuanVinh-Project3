package K23CNT1.NguyenXuanVinh.nxventity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "ShipmentDetails")
@Data
public class nxvShipmentDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer detailId;

    @ManyToOne
    @JoinColumn(name = "ShipmentID")
    private nxvShipmentRequest shipmentRequest;

    @ManyToOne
    @JoinColumn(name = "InventoryID")
    private nxvUserInventory userInventory;
}