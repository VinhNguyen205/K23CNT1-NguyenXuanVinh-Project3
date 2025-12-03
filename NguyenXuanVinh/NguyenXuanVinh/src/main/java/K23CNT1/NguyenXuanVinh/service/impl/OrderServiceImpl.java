package K23CNT1.NguyenXuanVinh.service.impl;

import K23CNT1.NguyenXuanVinh.entity.*;
import K23CNT1.NguyenXuanVinh.repository.*;
import K23CNT1.NguyenXuanVinh.service.OrderService;
import K23CNT1.NguyenXuanVinh.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired private CartRepository cartRepository;
    @Autowired private CartItemRepository cartItemRepository;
    @Autowired private OrderRepository orderRepository;
    @Autowired private OrderDetailRepository orderDetailRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private ShoppingCartService shoppingCartService;

    // --> THÊM CÁC REPO NÀY ĐỂ XỬ LÝ GAME LOGIC
    @Autowired private BoxItemRepository boxItemRepository;
    @Autowired private UserInventoryRepository userInventoryRepository;
    @Autowired private UserPityStatRepository userPityStatRepository;
    @Autowired private TransactionRepository transactionRepository;

    private final Random random = new Random();

    @Override
    @Transactional
    public Order placeOrder(Integer userId, String receiverName, String phoneNumber, String address) {
        // 1. Lấy thông tin
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Giỏ hàng trống!"));

        List<CartItem> items = cartItemRepository.findByCart(cart);
        if (items.isEmpty()) {
            throw new RuntimeException("Giỏ hàng không có gì để thanh toán!");
        }

        // 2. Tính tổng tiền
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (CartItem item : items) {
            if (item.getBlindBox() != null) {
                BigDecimal price = item.getBlindBox().getPrice();
                BigDecimal quantity = new BigDecimal(item.getQuantity());
                totalAmount = totalAmount.add(price.multiply(quantity));
            }
        }

        // 3. KIỂM TRA VÀ TRỪ TIỀN (Quan trọng!)
        if (user.getWalletBalance().compareTo(totalAmount) < 0) {
            throw new RuntimeException("Số dư không đủ! Vui lòng nạp thêm tiền.");
        }
        user.setWalletBalance(user.getWalletBalance().subtract(totalAmount));
        userRepository.save(user);

        // 4. Ghi lịch sử giao dịch (Transaction)
        Transaction trans = new Transaction();
        trans.setUser(user);
        trans.setAmount(totalAmount.negate());
        trans.setTransactionType("ORDER_PAYMENT");
        trans.setDescription("Thanh toán đơn hàng (Giỏ hàng)");
        transactionRepository.save(trans);

        // 5. Tạo Đơn hàng (Order)
        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setTotalAmount(totalAmount);
        order.setReceiverName(receiverName);
        order.setPhoneNumber(phoneNumber);
        order.setShippingAddress(address);
        order.setOrderStatus("COMPLETED"); // Đã thanh toán và mở hộp luôn
        order.setPaymentMethod("WALLET");  // Trừ ví
        order.setPaymentStatus("PAID");

        Order savedOrder = orderRepository.save(order);

        // 6. XỬ LÝ MỞ HỘP VÀ TRẢ ĐỒ VÀO KHO (QUAN TRỌNG NHẤT)
        for (CartItem item : items) {
            if (item.getBlindBox() != null) {
                BlindBox box = item.getBlindBox();

                // Lưu chi tiết đơn hàng
                OrderDetail detail = new OrderDetail();
                detail.setOrder(savedOrder);
                detail.setBlindBox(box);
                detail.setQuantity(item.getQuantity());
                detail.setPriceAtTime(box.getPrice());
                orderDetailRepository.save(detail);

                // --- LOGIC MỞ HỘP (Chạy vòng lặp theo số lượng mua) ---
                List<BoxItem> boxItems = boxItemRepository.findByBlindBox(box);

                for (int i = 0; i < item.getQuantity(); i++) {
                    // Xử lý bảo hiểm
                    UserPityStat pityStat = userPityStatRepository.findByUserAndBlindBox(user, box)
                            .orElseGet(() -> {
                                UserPityStat newStat = new UserPityStat();
                                newStat.setUser(user);
                                newStat.setBlindBox(box);
                                newStat.setSpinsWithoutS(0);
                                return userPityStatRepository.save(newStat);
                            });

                    // Random ra món đồ
                    BoxItem selectedItem;
                    if (pityStat.getSpinsWithoutS() >= 50) {
                        selectedItem = boxItems.stream()
                                .filter(bi -> "S".equals(bi.getRarityLevel()))
                                .findFirst().orElse(boxItems.get(0));
                        pityStat.setSpinsWithoutS(0);
                    } else {
                        selectedItem = performRandomDrop(boxItems);
                        if ("S".equals(selectedItem.getRarityLevel())) pityStat.setSpinsWithoutS(0);
                        else pityStat.setSpinsWithoutS(pityStat.getSpinsWithoutS() + 1);
                    }
                    userPityStatRepository.save(pityStat);

                    // LƯU VÀO KHO (UserInventory)
                    UserInventory inventory = new UserInventory();
                    inventory.setUser(user);
                    inventory.setBoxItem(selectedItem);
                    inventory.setStatus("IN_STORAGE");
                    userInventoryRepository.save(inventory);
                }
            }
        }

        // 7. Xóa sạch giỏ hàng
        shoppingCartService.clearCart(userId);

        return savedOrder;
    }

    // Hàm Random (Copy từ BlindBoxService sang để dùng chung)
    private BoxItem performRandomDrop(List<BoxItem> items) {
        double totalWeight = 0.0;
        for (BoxItem item : items) {
            totalWeight += (item.getProbability() != null) ? item.getProbability() : 0;
        }
        double randomValue = random.nextDouble() * totalWeight;
        double currentSum = 0.0;
        for (BoxItem item : items) {
            currentSum += (item.getProbability() != null) ? item.getProbability() : 0;
            if (randomValue <= currentSum) {
                return item;
            }
        }
        return items.get(items.size() - 1);
    }
}