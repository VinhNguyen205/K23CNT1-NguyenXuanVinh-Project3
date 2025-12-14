package K23CNT1.NguyenXuanVinh.nxventity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "ShipmentRequests")
@Data
public class nxvShipmentRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer shipmentId;

    @ManyToOne
    @JoinColumn(name = "UserID")
    private nxvUser user;

    private String receiverName;
    private String phoneNumber;
    private String address;
    private String note;

    private LocalDateTime requestDate = LocalDateTime.now();
    private String shipmentStatus; // PENDING, SHIPPING, DELIVERED, CANCELLED

    @OneToMany(mappedBy = "shipmentRequest", cascade = CascadeType.ALL)
    private List<nxvShipmentDetail> shipmentDetails;
}