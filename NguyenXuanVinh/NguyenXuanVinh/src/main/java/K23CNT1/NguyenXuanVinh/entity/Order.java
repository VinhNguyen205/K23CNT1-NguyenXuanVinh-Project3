package K23CNT1.NguyenXuanVinh.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "Orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "OrderID")
    private Integer orderId;

    @ManyToOne
    @JoinColumn(name = "UserID")
    private User user;

    @Column(name = "OrderDate")
    private LocalDateTime orderDate;

    @Column(name = "TotalAmount")
    private BigDecimal totalAmount;

    @Column(name = "ReceiverName")
    private String receiverName;

    @Column(name = "PhoneNumber")
    private String phoneNumber;

    @Column(name = "ShippingAddress")
    private String shippingAddress;

    @Column(name = "PaymentMethod") // COD, VNPAY
    private String paymentMethod;

    @Column(name = "PaymentStatus") // PAID, UNPAID
    private String paymentStatus;

    @Column(name = "OrderStatus") // PENDING, SHIPPING, COMPLETED
    private String orderStatus;

    @PrePersist
    protected void onCreate() {
        orderDate = LocalDateTime.now();
        if(orderStatus == null) orderStatus = "PENDING";
    }
}