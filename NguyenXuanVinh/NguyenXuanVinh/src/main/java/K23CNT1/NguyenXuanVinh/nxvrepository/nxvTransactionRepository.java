package K23CNT1.NguyenXuanVinh.nxvrepository;
import K23CNT1.NguyenXuanVinh.nxvdto.nxvTopUserDTO;
import K23CNT1.NguyenXuanVinh.nxventity.nxvTransaction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.util.List;

public interface nxvTransactionRepository extends JpaRepository<nxvTransaction, Integer> {
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM nxvTransaction t WHERE t.transactionType IN :types")
    BigDecimal sumTotalByTypes(@Param("types") List<String> types);

    @Query("SELECT new K23CNT1.NguyenXuanVinh.nxvdto.nxvTopUserDTO(t.user, SUM(t.amount)) FROM nxvTransaction t WHERE t.transactionType IN ('DEPOSIT', 'ADMIN_DEPOSIT') GROUP BY t.user ORDER BY SUM(t.amount) DESC")
    List<nxvTopUserDTO> findTopDepositors(Pageable pageable);
}