package K23CNT1.NguyenXuanVinh.nxvservice;

import K23CNT1.NguyenXuanVinh.nxvdto.nxvDashboardStats;
import K23CNT1.NguyenXuanVinh.nxvdto.nxvTopUserDTO;
import K23CNT1.NguyenXuanVinh.nxventity.*;
import K23CNT1.NguyenXuanVinh.nxvrepository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class nxvAdminService {
    private final nxvUserRepository nxvUserRepository;
    private final nxvTransactionRepository nxvTransactionRepository;
    private final nxvBlindBoxRepository nxvBlindBoxRepository;

    public nxvDashboardStats getDashboardStats() {
        return nxvDashboardStats.builder()
                .totalUsers(nxvUserRepository.count())
                .totalDeposit(nxvTransactionRepository.sumTotalByTypes(List.of("DEPOSIT", "ADMIN_DEPOSIT")))
                .totalSpent(nxvTransactionRepository.sumTotalByTypes(List.of("BUY_BOX", "ORDER_PAYMENT")).abs())
                .build();
    }

    public List<nxvTopUserDTO> getTopDepositors() {
        return nxvTransactionRepository.findTopDepositors(PageRequest.of(0, 5));
    }

    @Transactional
    public void addMoneyToUser(Integer userId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) return;
        nxvUser user = nxvUserRepository.findById(userId).orElseThrow();
        user.setWalletBalance(user.getWalletBalance().add(amount));
        nxvUserRepository.save(user);

        nxvTransaction t = new nxvTransaction();
        t.setUser(user); t.setAmount(amount); t.setTransactionType("ADMIN_DEPOSIT");
        nxvTransactionRepository.save(t);
    }

    public List<nxvBlindBox> getAllBoxes() { return nxvBlindBoxRepository.findAll(); }

    public void saveBox(nxvBlindBox box) {
        if(box.getImageUrl() == null || box.getImageUrl().isEmpty()) box.setImageUrl("/images/box.jpg");
        if(box.getIsActive() == null) box.setIsActive(true);
        nxvBlindBoxRepository.save(box);
    }

    public void deleteBox(Integer id) { nxvBlindBoxRepository.deleteById(id); }
}