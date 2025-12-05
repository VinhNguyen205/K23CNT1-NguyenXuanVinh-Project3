package K23CNT1.NguyenXuanVinh.nxventity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "Orders")
public class nxvOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "OrderID")
    private Integer orderId;

    @ManyToOne
    @JoinColumn(name = "UserID")
    private nxvUser user;

    @Column(name = "OrderDate")
    private LocalDateTime orderDate;

    @Column(name = "TotalAmount", precision = 18, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "ReceiverName")
    private String receiverName;

    @Column(name = "PhoneNumber")
    private String phoneNumber;

    @Column(name = "ShippingAddress")
    private String shippingAddress;

    @Column(name = "PaymentMethod")
    private String paymentMethod;

    @Column(name = "PaymentStatus")
    private String paymentStatus;

    @Column(name = "OrderStatus")
    private String orderStatus;
}