package K23CNT1.NguyenXuanVinh.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "BoxItems")
public class BoxItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ItemID")
    private Integer itemId;

    // Khóa ngoại trỏ đến BlindBox
    @ManyToOne
    @JoinColumn(name = "BoxID", nullable = false)
    private BlindBox blindBox;

    @Column(name = "ItemName", nullable = false, length = 200)
    private String itemName;

    @Column(name = "ImageURL", columnDefinition = "NVARCHAR(MAX)")
    private String imageUrl;

    @Column(name = "RarityLevel", nullable = false, length = 1)
    private String rarityLevel; // S, A, B, C, D

    @Column(name = "Probability")
    private Double probability; // 0.01

    @Column(name = "MarketValue", precision = 18, scale = 2)
    private BigDecimal marketValue;

    @Column(name = "StockQuantity")
    private Integer stockQuantity;

    @Column(name = "IsHidden")
    private Boolean isHidden;

    @Column(name = "IsPityReward")
    private Boolean isPityReward;
}