package K23CNT1.NguyenXuanVinh.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "ShipmentRequests")
public class ShipmentRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ShipmentID")
    private Integer shipmentId;

    @ManyToOne
    @JoinColumn(name = "UserID")
    private User user;

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
    private String shipmentStatus; // PENDING...

    @PrePersist
    protected void onCreate() {
        requestDate = LocalDateTime.now();
        if (shipmentStatus == null) shipmentStatus = "PENDING";
    }
}