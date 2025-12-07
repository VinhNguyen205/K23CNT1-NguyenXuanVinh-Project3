package K23CNT1.NguyenXuanVinh.nxvcontroller;

import K23CNT1.NguyenXuanVinh.nxventity.*;
import K23CNT1.NguyenXuanVinh.nxvrepository.*;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor // Tối ưu: Tự động tạo Constructor cho các biến final
public class nxvWebController {

    // Khai báo final để đảm bảo tính bất biến (Immutability)
    private final nxvBlindBoxRepository nxvBlindBoxRepository;
    private final nxvUserRepository nxvUserRepository;
    private final nxvUserInventoryRepository nxvUserInventoryRepository;
    private final nxvCartRepository nxvCartRepository;
    private final nxvCartItemRepository nxvCartItemRepository;
    private final nxvTransactionRepository nxvTransactionRepository;
    private final nxvBoxItemRepository nxvBoxItemRepository;
    private final nxvOrderRepository nxvOrderRepository;

    // Các Repository bổ sung cho giao diện đẹp (Banner, News, Category)
    private final nxvBannerRepository nxvBannerRepository;
    private final nxvNewsRepository nxvNewsRepository;
    private final nxvCategoryRepository nxvCategoryRepository;

    // Helper: Lấy user hiện tại và refresh từ DB để đảm bảo số dư ví luôn đúng
    private nxvUser getCurrentUser(HttpSession session) {
        Object sessionUser = session.getAttribute("currentUser");
        if (sessionUser instanceof nxvUser) {
            nxvUser user = (nxvUser) sessionUser;
            // Load lại từ DB để lấy WalletBalance mới nhất
            user = nxvUserRepository.findById(user.getUserId()).orElse(null);
            if (user != null) {
                session.setAttribute("currentUser", user); // Cập nhật lại session
                return user;
            }
        }
        return null;
    }

    // --- 1. TRANG CHỦ (HOME) ---
    @GetMapping("/")
    public String home(Model model, HttpSession session, @RequestParam(required = false) String keyword) {
        model.addAttribute("nxvUser", getCurrentUser(session));

        // 1. Logic tìm kiếm / lấy danh sách hộp
        List<nxvBlindBox> boxes;
        if (keyword != null && !keyword.isEmpty()) {
            boxes = nxvBlindBoxRepository.findByBoxNameContaining(keyword);
        } else {
            boxes = nxvBlindBoxRepository.findAll();
        }
        model.addAttribute("boxes", boxes);
        model.addAttribute("keyword", keyword);

        // 2. Tối ưu: Load thêm Banner, News, Category để trang chủ sinh động
        model.addAttribute("banners", nxvBannerRepository.findAllByOrderByDisplayOrderAsc());
        model.addAttribute("newsList", nxvNewsRepository.findAllByOrderByPublishedAtDesc());
        model.addAttribute("categories", nxvCategoryRepository.findAll());

        return "index";
    }

    // --- 2. CHI TIẾT SẢN PHẨM (BOX DETAIL) ---
    @GetMapping("/box/{id}")
    public String boxDetail(@PathVariable Integer id, Model model, HttpSession session) {
        model.addAttribute("nxvUser", getCurrentUser(session));

        nxvBlindBox box = nxvBlindBoxRepository.findById(id).orElse(null);
        if (box == null) return "redirect:/"; // Nếu id không tồn tại thì về trang chủ

        model.addAttribute("box", box);

        // Lấy danh sách item và sắp xếp theo độ hiếm
        List<nxvBoxItem> items = nxvBoxItemRepository.findByBlindBox(box);
        if (items != null) {
            items.sort((a, b) -> {
                String order = "SABCD"; // Thứ tự ưu tiên
                // Xử lý null safe để tránh lỗi nếu item chưa có rarity
                String rankStrA = a.getRarityLevel() != null ? a.getRarityLevel() : "Z";
                String rankStrB = b.getRarityLevel() != null ? b.getRarityLevel() : "Z";

                int rankA = order.indexOf(rankStrA);
                int rankB = order.indexOf(rankStrB);

                if (rankA == -1) rankA = 99; // Đẩy item lạ xuống cuối
                if (rankB == -1) rankB = 99;
                return Integer.compare(rankA, rankB);
            });
        }
        model.addAttribute("items", items);
        return "box-detail";
    }

    // --- 3. KHO ĐỒ (INVENTORY) ---
    @GetMapping("/inventory")
    public String inventory(Model model, HttpSession session) {
        nxvUser user = getCurrentUser(session);
        if (user == null) return "redirect:/login";

        model.addAttribute("nxvUser", user);
        // Chỉ hiện vật phẩm đang "IN_STORAGE"
        model.addAttribute("inventoryList", nxvUserInventoryRepository.findByUserAndStatus(user, "IN_STORAGE"));
        return "inventory";
    }

    // --- 4. GIỎ HÀNG (CART) ---
    @GetMapping("/cart")
    public String viewCart(Model model, HttpSession session) {
        nxvUser user = getCurrentUser(session);
        if (user == null) return "redirect:/login";

        model.addAttribute("nxvUser", user);

        nxvCart cart = nxvCartRepository.findByUser(user).orElse(null);
        List<nxvCartItem> cartItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO; // Tối ưu: Dùng BigDecimal thay vì double

        if (cart != null) {
            cartItems = nxvCartItemRepository.findByCart(cart);

            // Tính tổng tiền bằng BigDecimal để chính xác
            for (nxvCartItem item : cartItems) {
                if (item.getBlindBox() != null) {
                    BigDecimal price = item.getBlindBox().getPrice();
                    BigDecimal quantity = BigDecimal.valueOf(item.getQuantity());
                    totalAmount = totalAmount.add(price.multiply(quantity));
                }
            }
        }

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalAmount", totalAmount);
        return "cart";
    }

    // --- 5. NẠP TIỀN (DEPOSIT) ---
    @GetMapping("/deposit")
    public String depositPage(Model model, HttpSession session) {
        nxvUser user = getCurrentUser(session);
        if (user == null) return "redirect:/login";

        model.addAttribute("nxvUser", user);
        return "deposit";
    }

    @PostMapping("/deposit")
    public String processDeposit(@RequestParam BigDecimal amount, HttpSession session) {
        nxvUser user = getCurrentUser(session); // Dùng hàm helper để lấy user mới nhất
        if (user == null) return "redirect:/login";

        if (amount != null && amount.compareTo(BigDecimal.ZERO) > 0) {
            // Cộng tiền
            user.setWalletBalance(user.getWalletBalance().add(amount));
            nxvUserRepository.save(user);

            // Ghi log giao dịch
            nxvTransaction t = new nxvTransaction();
            t.setUser(user);
            t.setAmount(amount);
            t.setTransactionType("DEPOSIT");
            t.setDescription("Nạp tiền vào ví");
            nxvTransactionRepository.save(t);

            // Cập nhật session
            session.setAttribute("currentUser", user);
        }
        return "redirect:/";
    }

    // --- 6. HỒ SƠ & LỊCH SỬ ĐƠN HÀNG (PROFILE) ---
    @GetMapping("/profile")
    public String profile(Model model, HttpSession session) {
        nxvUser user = getCurrentUser(session);
        if (user == null) return "redirect:/login";

        model.addAttribute("nxvUser", user);
        // Lấy lịch sử mua hàng
        model.addAttribute("orders", nxvOrderRepository.findByUserOrderByOrderDateDesc(user));
        return "profile";
    }
}