package K23CNT1.NguyenXuanVinh.controller;

import K23CNT1.NguyenXuanVinh.entity.*;
import K23CNT1.NguyenXuanVinh.repository.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Controller
public class WebController {

    // --- KHAI BÁO CÁC REPOSITORY ---
    @Autowired private BlindBoxRepository blindBoxRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private UserInventoryRepository userInventoryRepository;
    @Autowired private CartRepository cartRepository;
    @Autowired private CartItemRepository cartItemRepository;
    @Autowired private TransactionRepository transactionRepository;
    @Autowired private BoxItemRepository boxItemRepository;
    @Autowired private OrderRepository orderRepository; // Mới thêm cho trang Profile

    // ==============================================================
    // 1. TRANG CHỦ (HOME PAGE)
    // ==============================================================
    @GetMapping("/")
    public String home(Model model, HttpSession session,
                       @RequestParam(required = false) String keyword) { // Thêm biến keyword

        User user = (User) session.getAttribute("currentUser");
        if (user != null) {
            user = userRepository.findById(user.getUserId()).orElse(user);
            session.setAttribute("currentUser", user);
        }
        model.addAttribute("user", user);

        // TÌM KIẾM:
        List<BlindBox> boxes;
        if (keyword != null && !keyword.isEmpty()) {
            // Nếu có từ khóa -> Tìm theo tên
            boxes = blindBoxRepository.findByBoxNameContaining(keyword);
        } else {
            // Nếu không -> Lấy hết
            boxes = blindBoxRepository.findAll();
        }

        model.addAttribute("boxes", boxes);
        model.addAttribute("keyword", keyword); // Gửi lại từ khóa để hiện lên ô input

        return "index";
    }

    // ==============================================================
    // 2. TRANG CHI TIẾT SẢN PHẨM (PRODUCT DETAIL)
    // URL: http://localhost:8080/box/{id}
    // ==============================================================
    @GetMapping("/box/{id}")
    public String boxDetail(@PathVariable Integer id, Model model, HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user != null) {
            user = userRepository.findById(user.getUserId()).orElse(user);
            session.setAttribute("currentUser", user);
        }
        model.addAttribute("user", user);

        BlindBox box = blindBoxRepository.findById(id).orElse(null);
        if (box == null) return "redirect:/";

        model.addAttribute("box", box);

        // Lấy danh sách vật phẩm trong hộp, sắp xếp theo độ hiếm S->A->B->C->D
        List<BoxItem> items = boxItemRepository.findByBlindBox(box);
        items.sort((a, b) -> {
            String order = "SABCD";
            int rankA = order.indexOf(a.getRarityLevel());
            int rankB = order.indexOf(b.getRarityLevel());
            if (rankA == -1) rankA = 99;
            if (rankB == -1) rankB = 99;
            return Integer.compare(rankA, rankB);
        });

        model.addAttribute("items", items);
        return "box-detail"; // Trả về templates/box-detail.html
    }

    // ==============================================================
    // 3. TRANG KHO ĐỒ (INVENTORY)
    // URL: http://localhost:8080/inventory
    // ==============================================================
    @GetMapping("/inventory")
    public String inventory(Model model, HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) return "redirect:/login";

        user = userRepository.findById(user.getUserId()).orElse(user);
        model.addAttribute("user", user);

        // Chỉ hiện món đang có trong kho (IN_STORAGE)
        model.addAttribute("inventoryList", userInventoryRepository.findByUserAndStatus(user, "IN_STORAGE"));
        return "inventory"; // Trả về templates/inventory.html
    }

    // ==============================================================
    // 4. TRANG GIỎ HÀNG (SHOPPING CART)
    // URL: http://localhost:8080/cart
    // ==============================================================
    @GetMapping("/cart")
    public String viewCart(Model model, HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) return "redirect:/login";

        user = userRepository.findById(user.getUserId()).orElse(user);
        model.addAttribute("user", user);

        Cart cart = cartRepository.findByUser(user).orElse(null);
        List<CartItem> cartItems = new ArrayList<>();
        double totalAmount = 0.0;

        if (cart != null) {
            cartItems = cartItemRepository.findByCart(cart);
            totalAmount = cartItems.stream().mapToDouble(item ->
                    (item.getBlindBox() != null ? item.getBlindBox().getPrice().doubleValue() : 0) * item.getQuantity()
            ).sum();
        }

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalAmount", totalAmount);
        return "cart"; // Trả về templates/cart.html
    }

    // ==============================================================
    // 5. TRANG NẠP TIỀN (DEPOSIT)
    // URL: http://localhost:8080/deposit
    // ==============================================================
    @GetMapping("/deposit")
    public String depositPage(Model model, HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) return "redirect:/login";
        model.addAttribute("user", user);
        return "deposit"; // Trả về templates/deposit.html
    }

    @PostMapping("/deposit")
    public String processDeposit(@RequestParam BigDecimal amount, HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) return "redirect:/login";

        if (amount.compareTo(BigDecimal.ZERO) > 0) {
            User dbUser = userRepository.findById(user.getUserId()).get();
            // Cộng tiền
            dbUser.setWalletBalance(dbUser.getWalletBalance().add(amount));
            userRepository.save(dbUser);

            // Lưu lịch sử
            Transaction t = new Transaction();
            t.setUser(dbUser);
            t.setAmount(amount);
            t.setTransactionType("DEPOSIT");
            t.setDescription("Nạp tiền qua cổng thanh toán");
            transactionRepository.save(t);

            // Cập nhật session
            session.setAttribute("currentUser", dbUser);
        }
        return "redirect:/";
    }

    // ==============================================================
    // 6. TRANG CÁ NHÂN & LỊCH SỬ ĐƠN HÀNG (PROFILE)
    // URL: http://localhost:8080/profile
    // ==============================================================
    @GetMapping("/profile")
    public String profile(Model model, HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) return "redirect:/login";

        // Cập nhật user mới nhất
        user = userRepository.findById(user.getUserId()).orElse(user);
        model.addAttribute("user", user);

        // Lấy danh sách đơn hàng đã đặt
        model.addAttribute("orders", orderRepository.findByUserOrderByOrderDateDesc(user));

        return "profile"; // Trả về templates/profile.html
    }
}