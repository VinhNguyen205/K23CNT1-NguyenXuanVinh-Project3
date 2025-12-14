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

        // Inject tất cả Repository
        private final nxvUserRepository nxvUserRepository;
        private final nxvTransactionRepository nxvTransactionRepository;
        private final nxvBlindBoxRepository nxvBlindBoxRepository;
        private final nxvOrderRepository nxvOrderRepository;
        private final nxvCategoryRepository nxvCategoryRepository;
        private final nxvNewsRepository nxvNewsRepository;
        private final nxvBannerRepository nxvBannerRepository;     // Mới
        private final nxvFeedbackRepository nxvFeedbackRepository; // Mới
        private final nxvBoxItemRepository nxvBoxItemRepository;   // Mới (Kho)

        // --- 1. DASHBOARD ---
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

        // --- 2. USER & MONEY ---
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

        // --- 3. BLIND BOX ---
        public List<nxvBlindBox> getAllBoxes() { return nxvBlindBoxRepository.findAll(); }
        public void saveBox(nxvBlindBox box) {
            if(box.getImageUrl() == null || box.getImageUrl().isEmpty()) box.setImageUrl("/images/box.jpg");
            if(box.getIsActive() == null) box.setIsActive(true);
            nxvBlindBoxRepository.save(box);
        }
        public void deleteBox(Integer id) { nxvBlindBoxRepository.deleteById(id); }

        // --- 4. ORDERS ---
        public List<nxvOrder> getAllOrders() {
            return nxvOrderRepository.findAllByOrderByOrderDateDesc();
        }

        public void updateOrderStatus(Integer orderId, String status) {
            nxvOrder order = nxvOrderRepository.findById(orderId).orElse(null);
            if (order != null) {
                order.setOrderStatus(status);
                nxvOrderRepository.save(order);
            }
        }

        // --- 5. CATEGORIES ---
        public List<nxvCategory> getAllCategories() { return nxvCategoryRepository.findAll(); }
        public void saveCategory(nxvCategory cat) {
            if(cat.getIsActive() == null) cat.setIsActive(true);
            nxvCategoryRepository.save(cat);
        }
        public void deleteCategory(Integer id) { nxvCategoryRepository.deleteById(id); }

        // --- 6. NEWS ---
        public List<nxvNews> getAllNews() { return nxvNewsRepository.findAllByOrderByPublishedAtDesc(); }
        public void saveNews(nxvNews news) { nxvNewsRepository.save(news); }
        public void deleteNews(Integer id) { nxvNewsRepository.deleteById(id); }

        // --- 7. BANNERS (MỚI) ---
        public List<nxvBanner> getAllBanners() { return nxvBannerRepository.findAllByOrderByDisplayOrderAsc(); }
        public void saveBanner(nxvBanner banner) { nxvBannerRepository.save(banner); }
        public void deleteBanner(Integer id) { nxvBannerRepository.deleteById(id); }

        // --- 8. FEEDBACKS (MỚI) ---
        public List<nxvFeedback> getAllFeedbacks() { return nxvFeedbackRepository.findAllByOrderBySentAtDesc(); }
        public void replyFeedback(Integer id, String replyContent) {
            nxvFeedback fb = nxvFeedbackRepository.findById(id).orElse(null);
            if(fb != null) {
                fb.setReplyContent(replyContent);
                fb.setStatus("RESOLVED"); // Đánh dấu đã giải quyết
                nxvFeedbackRepository.save(fb);
            }
        }

        // --- 9. INVENTORY/WAREHOUSE (MỚI) ---
        // Lấy danh sách item sắp hết hàng
        public List<nxvBoxItem> getLowStockItems() {
            return nxvBoxItemRepository.findLowStockItems();
        }
        // Cập nhật số lượng kho
        public void updateStock(Integer itemId, Integer newQuantity) {
            nxvBoxItem item = nxvBoxItemRepository.findById(itemId).orElse(null);
            if(item != null) {
                item.setStockQuantity(newQuantity);
                nxvBoxItemRepository.save(item);
            }
        }
    }