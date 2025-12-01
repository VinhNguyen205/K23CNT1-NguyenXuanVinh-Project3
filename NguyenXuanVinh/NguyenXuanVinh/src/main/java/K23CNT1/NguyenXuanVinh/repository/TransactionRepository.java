package K23CNT1.NguyenXuanVinh.repository;

import K23CNT1.NguyenXuanVinh.entity.Transaction;
import K23CNT1.NguyenXuanVinh.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

    // Lấy lịch sử giao dịch của user, sắp xếp mới nhất lên đầu (DESC)
    List<Transaction> findByUserOrderByTransactionDateDesc(User user);
}