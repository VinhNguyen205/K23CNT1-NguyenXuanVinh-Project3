package K23CNT1.NguyenXuanVinh.nxventity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "Orders") // Đổi lại thành 'Orders' cho khớp với SQL Script của bạn
@Data // Tự động sinh getter/setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class nxvOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "OrderID") // Khớp SQL
    private Integer id;

    // Quan hệ với User
    @ManyToOne
    @JoinColumn(name = "UserID", nullable = false) // Khớp SQL
    private nxvUser user;

    // Ngày đặt hàng
    @Column(name = "OrderDate") // Khớp SQL
    private LocalDateTime orderDate;

    // Tổng tiền
    @Column(name = "TotalAmount") // Khớp SQL
    private BigDecimal totalAmount;

    // --- CÁC TRƯỜNG MỚI THÊM ĐỂ SỬA LỖI SERVICE ---

    @Column(name = "ReceiverName")
    private String receiverName; // Sửa lỗi setReceiverName

    @Column(name = "PhoneNumber")
    private String phoneNumber;  // Sửa lỗi setPhoneNumber

    @Column(name = "ShippingAddress")
    private String shippingAddress; // Sửa lỗi setShippingAddress

    @Column(name = "OrderStatus")
    private String orderStatus;

    @Column(name = "PaymentMethod")
    private String paymentMethod; // COD, WALLET...

    @Column(name = "PaymentStatus")
    private String paymentStatus; // PAID, UNPAID

    @Column(name = "DeliveryDate")
    private LocalDateTime deliveryDate;

    // --- HÀM KHỞI TẠO MẶC ĐỊNH ---
    @PrePersist
    protected void onCreate() {
        if (this.orderDate == null) {
            this.orderDate = LocalDateTime.now();
        }
        if (this.orderStatus == null) {
            this.orderStatus = "PENDING";
        }
        if (this.paymentStatus == null) {
            this.paymentStatus = "UNPAID";
        }
    }
}