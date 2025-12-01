package K23CNT1.NguyenXuanVinh.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "Users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UserID")
    private Integer userId;

    @Column(name = "Username", nullable = false, unique = true, length = 50)
    private String username;

    @Column(name = "PasswordHash", nullable = false)
    private String passwordHash;

    @Column(name = "Email", unique = true, length = 100)
    private String email;

    @Column(name = "FullName", length = 100)
    private String fullName;

    @Column(name = "WalletBalance", precision = 18, scale = 2)
    private BigDecimal walletBalance;

    @Column(name = "CreatedAt")
    private LocalDateTime createdAt;

    @Column(name = "IsAdmin")
    private Boolean isAdmin;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (walletBalance == null) walletBalance = BigDecimal.ZERO;
        if (isAdmin == null) isAdmin = false;
    }
}