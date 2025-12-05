package K23CNT1.NguyenXuanVinh.nxventity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "Transactions")
public class nxvTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TransactionID")
    private Integer transactionId;

    @ManyToOne
    @JoinColumn(name = "UserID")
    private nxvUser user;

    @Column(name = "Amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal amount;

    @Column(name = "TransactionType")
    private String transactionType;

    @Column(name = "Description")
    private String description;

    @Column(name = "TransactionDate")
    private LocalDateTime transactionDate;

    @PrePersist
    protected void onCreate() {
        transactionDate = LocalDateTime.now();
    }
}