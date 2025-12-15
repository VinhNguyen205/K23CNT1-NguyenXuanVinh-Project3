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
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class nxvAdminService {

    // --- INJECT REPOSITORIES ---
    private final nxvUserRepository userRepository;
    private final nxvTransactionRepository transactionRepository;
    private final nxvBlindBoxRepository boxRepository;
    private final nxvOrderRepository orderRepository; // Đảm bảo bạn đã có Repo này (Map với bảng Orders)
    private final nxvCategoryRepository categoryRepository;
    private final nxvNewsRepository newsRepository;
    private final nxvBannerRepository bannerRepository;
    private final nxvFeedbackRepository feedbackRepository;
    private final nxvBoxItemRepository boxItemRepository;

    // ==========================================
    // 1. DASHBOARD & THỐNG KÊ
    // ==========================================
    public nxvDashboardStats getDashboardStats() {
        // Tổng User
        long totalUsers = userRepository.count();

        // Tổng tiền nạp (DEPOSIT + ADMIN_DEPOSIT)
        BigDecimal deposit = transactionRepository.sumAmountByType("DEPOSIT");
        BigDecimal adminDeposit = transactionRepository.sumAmountByType("ADMIN_DEPOSIT");
        BigDecimal totalDeposit = (deposit != null ? deposit : BigDecimal.ZERO)
                .add(adminDeposit != null ? adminDeposit : BigDecimal.ZERO);

        // Doanh thu thực (Tổng tiền mua box)
        BigDecimal spent = transactionRepository.sumAmountByType("BUY_BOX");
        BigDecimal totalSpent = (spent != null ? spent : BigDecimal.ZERO).abs(); // Lấy số dương

        return nxvDashboardStats.builder()
                .totalUsers(totalUsers)
                .totalDeposit(totalDeposit)
                .totalSpent(totalSpent)
                .build();
    }

    public List<nxvTopUserDTO> getTopDepositors() {
        // Lấy Top 5 đại gia
        return transactionRepository.findTopDepositors(PageRequest.of(0, 5));
    }

    // ==========================================
    // 2. QUẢN LÝ USER (NẠP TIỀN)
    // ==========================================
    @Transactional
    public void addMoneyToUser(Integer userId, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) return;

        nxvUser user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));

        user.setWalletBalance(user.getWalletBalance().add(amount));
        userRepository.save(user);

        // Ghi lịch sử giao dịch
        nxvTransaction t = new nxvTransaction();
        t.setUser(user);
        t.setAmount(amount);
        t.setTransactionType("ADMIN_DEPOSIT");
        t.setDescription("Admin nạp tiền: " + amount + "đ");
        t.setTransactionDate(LocalDateTime.now());
        transactionRepository.save(t);
    }

    // ==========================================
    // 3. QUẢN LÝ SẢN PHẨM (BLIND BOX)
    // ==========================================
    public List<nxvBlindBox> getAllBoxes() {
        return boxRepository.findAll();
    }

    @Transactional
    public void saveBox(nxvBlindBox box) {
        if (box.getCreatedAt() == null) box.setCreatedAt(LocalDateTime.now());
        if (box.getImageUrl() == null || box.getImageUrl().isEmpty()) box.setImageUrl("/images/box.jpg");
        if (box.getIsActive() == null) box.setIsActive(true);
        boxRepository.save(box);
    }

    @Transactional
    public void deleteBox(Integer id) {
        boxRepository.deleteById(id);
    }

    // ==========================================
    // 4. QUẢN LÝ ĐƠN HÀNG (ORDERS)
    // ==========================================
    public List<nxvOrder> getAllOrders() {
        return orderRepository.findAllByOrderByOrderDateDesc();
    }

    @Transactional
    public void updateOrderStatus(Integer orderId, String status) {
        nxvOrder order = orderRepository.findById(orderId).orElse(null);
        if (order != null) {
            order.setOrderStatus(status);
            if ("DELIVERED".equalsIgnoreCase(status)) {
                order.setDeliveryDate(LocalDateTime.now());
            }
            orderRepository.save(order);
        }
    }

    // ==========================================
    // 5. QUẢN LÝ DANH MỤC
    // ==========================================
    public List<nxvCategory> getAllCategories() {
        return categoryRepository.findAll();
    }

    public void saveCategory(nxvCategory cat) {
        if (cat.getIsActive() == null) cat.setIsActive(true);
        categoryRepository.save(cat);
    }

    public void deleteCategory(Integer id) {
        categoryRepository.deleteById(id);
    }

    // ==========================================
    // 6. TIN TỨC & BANNER
    // ==========================================
    public List<nxvNews> getAllNews() {
        return newsRepository.findAllByOrderByPublishedAtDesc();
    }

    public void saveNews(nxvNews news) {
        if (news.getPublishedAt() == null) news.setPublishedAt(LocalDateTime.now());
        newsRepository.save(news);
    }

    public void deleteNews(Integer id) {
        newsRepository.deleteById(id);
    }

    public List<nxvBanner> getAllBanners() {
        return bannerRepository.findAllByOrderByDisplayOrderAsc();
    }

    public void saveBanner(nxvBanner banner) {
        if (banner.getCreatedAt() == null) banner.setCreatedAt(LocalDateTime.now());
        bannerRepository.save(banner);
    }

    public void deleteBanner(Integer id) {
        bannerRepository.deleteById(id);
    }

    // ==========================================
    // 7. PHẢN HỒI (FEEDBACK)
    // ==========================================
    public List<nxvFeedback> getAllFeedbacks() {
        return feedbackRepository.findAllByOrderBySentAtDesc();
    }

    @Transactional
    public void replyFeedback(Integer id, String replyContent) {
        nxvFeedback fb = feedbackRepository.findById(id).orElse(null);
        if (fb != null) {
            fb.setReplyContent(replyContent);
            fb.setStatus("RESOLVED");
            fb.setRepliedAt(LocalDateTime.now());
            feedbackRepository.save(fb);
        }
    }

    // ==========================================
    // 8. KHO HÀNG (INVENTORY)
    // ==========================================
    public List<nxvBoxItem> getLowStockItems() {
        // Lấy danh sách item sắp hết hàng (Dưới 20)
        return boxItemRepository.findByStockQuantityLessThanOrderByStockQuantityAsc(20);
    }

    @Transactional
    public void updateStock(Integer itemId, Integer quantity) {
        nxvBoxItem item = boxItemRepository.findById(itemId).orElse(null);
        if (item != null) {
            if (quantity < 0) quantity = 0;
            item.setStockQuantity(quantity);
            boxItemRepository.save(item);
        }
    }
}