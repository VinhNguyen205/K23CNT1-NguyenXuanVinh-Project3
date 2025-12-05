package K23CNT1.NguyenXuanVinh.nxvcontroller;

import K23CNT1.NguyenXuanVinh.nxventity.*;
import K23CNT1.NguyenXuanVinh.nxvrepository.*;
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
public class nxvWebController {

    @Autowired private nxvBlindBoxRepository nxvBlindBoxRepository;
    @Autowired private nxvUserRepository nxvUserRepository;
    @Autowired private nxvUserInventoryRepository nxvUserInventoryRepository;
    @Autowired private nxvCartRepository nxvCartRepository;
    @Autowired private nxvCartItemRepository nxvCartItemRepository;
    @Autowired private nxvTransactionRepository nxvTransactionRepository;
    @Autowired private nxvBoxItemRepository nxvBoxItemRepository;
    @Autowired private nxvOrderRepository nxvOrderRepository;

    // Helper: Lấy user hiện tại và refresh từ DB
    private nxvUser getCurrentUser(HttpSession session) {
        nxvUser user = (nxvUser) session.getAttribute("currentUser");
        if (user != null) {
            user = nxvUserRepository.findById(user.getUserId()).orElse(user);
            session.setAttribute("currentUser", user);
        }
        return user;
    }

    // 1. TRANG CHỦ
    @GetMapping("/")
    public String home(Model model, HttpSession session, @RequestParam(required = false) String keyword) {
        // ĐỔI TÊN BIẾN GỬI RA VIEW THÀNH "nxvUser"
        model.addAttribute("nxvUser", getCurrentUser(session));

        List<nxvBlindBox> boxes;
        if (keyword != null && !keyword.isEmpty()) {
            boxes = nxvBlindBoxRepository.findByBoxNameContaining(keyword);
        } else {
            boxes = nxvBlindBoxRepository.findAll();
        }
        model.addAttribute("boxes", boxes);
        model.addAttribute("keyword", keyword);
        return "index";
    }

    // 2. CHI TIẾT SẢN PHẨM
    @GetMapping("/box/{id}")
    public String boxDetail(@PathVariable Integer id, Model model, HttpSession session) {
        model.addAttribute("nxvUser", getCurrentUser(session)); // Đổi tên

        nxvBlindBox box = nxvBlindBoxRepository.findById(id).orElse(null);
        if (box == null) return "redirect:/";

        model.addAttribute("box", box);
        List<nxvBoxItem> items = nxvBoxItemRepository.findByBlindBox(box);
        items.sort((a, b) -> {
            String order = "SABCD";
            int rankA = order.indexOf(a.getRarityLevel());
            int rankB = order.indexOf(b.getRarityLevel());
            if (rankA == -1) rankA = 99;
            if (rankB == -1) rankB = 99;
            return Integer.compare(rankA, rankB);
        });
        model.addAttribute("items", items);
        return "box-detail";
    }

    // 3. KHO ĐỒ
    @GetMapping("/inventory")
    public String inventory(Model model, HttpSession session) {
        nxvUser user = getCurrentUser(session);
        if (user == null) return "redirect:/login";

        model.addAttribute("nxvUser", user); // Đổi tên
        model.addAttribute("inventoryList", nxvUserInventoryRepository.findByUserAndStatus(user, "IN_STORAGE"));
        return "inventory";
    }

    // 4. GIỎ HÀNG
    @GetMapping("/cart")
    public String viewCart(Model model, HttpSession session) {
        nxvUser user = getCurrentUser(session);
        if (user == null) return "redirect:/login";

        model.addAttribute("nxvUser", user); // Đổi tên

        nxvCart cart = nxvCartRepository.findByUser(user).orElse(null);
        List<nxvCartItem> cartItems = new ArrayList<>();
        double totalAmount = 0.0;

        if (cart != null) {
            cartItems = nxvCartItemRepository.findByCart(cart);
            totalAmount = cartItems.stream().mapToDouble(item ->
                    (item.getBlindBox() != null ? item.getBlindBox().getPrice().doubleValue() : 0) * item.getQuantity()
            ).sum();
        }

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalAmount", totalAmount);
        return "cart";
    }

    // 5. NẠP TIỀN
    @GetMapping("/deposit")
    public String depositPage(Model model, HttpSession session) {
        nxvUser user = getCurrentUser(session);
        if (user == null) return "redirect:/login";
        model.addAttribute("nxvUser", user); // Đổi tên
        return "deposit";
    }

    @PostMapping("/deposit")
    public String processDeposit(@RequestParam BigDecimal amount, HttpSession session) {
        nxvUser user = (nxvUser) session.getAttribute("currentUser");
        if (user == null) return "redirect:/login";

        if (amount.compareTo(BigDecimal.ZERO) > 0) {
            nxvUser dbUser = nxvUserRepository.findById(user.getUserId()).get();
            dbUser.setWalletBalance(dbUser.getWalletBalance().add(amount));
            nxvUserRepository.save(dbUser);

            nxvTransaction t = new nxvTransaction();
            t.setUser(dbUser);
            t.setAmount(amount);
            t.setTransactionType("DEPOSIT");
            t.setDescription("Nạp tiền");
            nxvTransactionRepository.save(t);

            session.setAttribute("currentUser", dbUser);
        }
        return "redirect:/";
    }

    // 6. PROFILE
    @GetMapping("/profile")
    public String profile(Model model, HttpSession session) {
        nxvUser user = getCurrentUser(session);
        if (user == null) return "redirect:/login";

        model.addAttribute("nxvUser", user); // Đổi tên
        model.addAttribute("orders", nxvOrderRepository.findByUserOrderByOrderDateDesc(user));
        return "profile";
    }
}