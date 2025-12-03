package K23CNT1.NguyenXuanVinh.repository;

import K23CNT1.NguyenXuanVinh.dto.TopUserDTO;
import K23CNT1.NguyenXuanVinh.entity.Transaction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

    // Tối ưu: Dùng list type để gộp DEPOSIT và ADMIN_DEPOSIT trong 1 câu lệnh
    // COALESCE: Nếu null thì trả về 0
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.transactionType IN :types")
    BigDecimal sumTotalByTypes(@Param("types") List<String> types);

    // Lấy Top Đại Gia
    @Query("SELECT new K23CNT1.NguyenXuanVinh.dto.TopUserDTO(t.user, SUM(t.amount)) " +
            "FROM Transaction t " +
            "WHERE t.transactionType IN ('DEPOSIT', 'ADMIN_DEPOSIT') " +
            "GROUP BY t.user " +
            "ORDER BY SUM(t.amount) DESC")
    List<TopUserDTO> findTopDepositors(Pageable pageable);
}