package K23CNT1.NguyenXuanVinh.service;

import K23CNT1.NguyenXuanVinh.dto.DashboardStats;
import K23CNT1.NguyenXuanVinh.dto.TopUserDTO;
import K23CNT1.NguyenXuanVinh.entity.BlindBox;
import K23CNT1.NguyenXuanVinh.entity.Transaction;
import K23CNT1.NguyenXuanVinh.entity.User;
import K23CNT1.NguyenXuanVinh.repository.BlindBoxRepository;
import K23CNT1.NguyenXuanVinh.repository.TransactionRepository;
import K23CNT1.NguyenXuanVinh.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor // Tự động Inject Repository (Không cần @Autowired)
public class AdminService {

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final BlindBoxRepository blindBoxRepository;

    // 1. Lấy số liệu thống kê
    public DashboardStats getDashboardStats() {
        return DashboardStats.builder()
                .totalUsers(userRepository.count())
                .totalDeposit(transactionRepository.sumTotalByTypes(List.of("DEPOSIT", "ADMIN_DEPOSIT")))
                .totalSpent(transactionRepository.sumTotalByTypes(List.of("BUY_BOX", "ORDER_PAYMENT")).abs()) // Lấy trị tuyệt đối
                .build();
    }

    // 2. Lấy Top đại gia
    public List<TopUserDTO> getTopDepositors() {
        return transactionRepository.findTopDepositors(PageRequest.of(0, 5));
    }

    // 3. Bơm tiền (Transaction chuẩn ACID)
    @Transactional
    public void addMoneyToUser(Integer userId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) return;

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setWalletBalance(user.getWalletBalance().add(amount));
        userRepository.save(user);

        // Ghi log ngay tại đây
        Transaction t = new Transaction();
        t.setUser(user);
        t.setAmount(amount);
        t.setTransactionType("ADMIN_DEPOSIT");
        t.setDescription("Admin lì xì");
        transactionRepository.save(t);
    }

    // 4. Các hàm CRUD Hộp
    public List<BlindBox> getAllBoxes() { return blindBoxRepository.findAll(); }

    public void saveBox(BlindBox box) {
        if(box.getImageUrl() == null || box.getImageUrl().isEmpty()) {
            box.setImageUrl("/images/box.jpg");
        }
        if(box.getIsActive() == null) box.setIsActive(true);
        blindBoxRepository.save(box);
    }

    public void deleteBox(Integer id) {
        blindBoxRepository.deleteById(id);
    }
}