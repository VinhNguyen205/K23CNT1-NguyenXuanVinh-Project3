package K23CNT1.NguyenXuanVinh.nxventity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "ShipmentDetails")
public class nxvShipmentDetail { // Đổi tên Class
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DetailID")
    private Integer detailId;

    @ManyToOne
    @JoinColumn(name = "ShipmentID")
    private nxvShipmentRequest nxvShipmentRequest; // Đổi tên Type và Variable

    // Link tới vật phẩm cụ thể trong kho
    @ManyToOne
    @JoinColumn(name = "InventoryID")
    private nxvUserInventory nxvUserInventory; // Đã có sẵn tiền tố
}