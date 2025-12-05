package K23CNT1.NguyenXuanVinh.nxventity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "ShipmentRequests")
public class nxvShipmentRequest { // Đổi tên Class
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ShipmentID")
    private Integer shipmentId;

    @ManyToOne
    @JoinColumn(name = "UserID")
    private nxvUser nxvUser; // Đã có sẵn tiền tố

    @Column(name = "ReceiverName", length = 100)
    private String receiverName;

    @Column(name = "PhoneNumber", length = 20)
    private String phoneNumber;

    @Column(name = "Address", columnDefinition = "NVARCHAR(MAX)")
    private String address;

    @Column(name = "Note", columnDefinition = "NVARCHAR(MAX)")
    private String note;

    @Column(name = "RequestDate")
    private LocalDateTime requestDate;

    @Column(name = "ShipmentStatus", length = 50)
    private String shipmentStatus;

    @PrePersist
    protected void onCreate() {
        requestDate = LocalDateTime.now();
        if (shipmentStatus == null) shipmentStatus = "PENDING";
    }
}