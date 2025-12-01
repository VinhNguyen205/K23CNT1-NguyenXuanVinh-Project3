package K23CNT1.NguyenXuanVinh.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "UserPityStats")
public class UserPityStat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "StatID")
    private Integer statId;

    @ManyToOne
    @JoinColumn(name = "UserID")
    private User user;

    @ManyToOne
    @JoinColumn(name = "BoxID")
    private BlindBox blindBox;

    @Column(name = "SpinsWithoutS")
    private Integer spinsWithoutS;

    @Column(name = "LastSRewardDate")
    private LocalDateTime lastSRewardDate;
}