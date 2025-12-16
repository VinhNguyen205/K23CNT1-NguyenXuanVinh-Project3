package K23CNT1.NguyenXuanVinh.nxvcontroller;

import K23CNT1.NguyenXuanVinh.nxventity.*;
import K23CNT1.NguyenXuanVinh.nxvrepository.nxvUserRepository;
import K23CNT1.NguyenXuanVinh.nxvservice.nxvAdminService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class nxvAdminController {

    private final nxvAdminService adminService;
    private final nxvUserRepository userRepository;

    // --- HELPER: CHECK ADMIN PERMISSIONS ---
    private nxvUser getAdminUser(HttpSession session) {
        Object sessionUser = session.getAttribute("currentUser");
        if (sessionUser instanceof nxvUser) {
            nxvUser user = (nxvUser) sessionUser;
            // Only return user if they are an Admin (IsAdmin = true)
            if (Boolean.TRUE.equals(user.getIsAdmin())) return user;
        }
        return null;
    }

    // ==========================================
    // 1. DASHBOARD
    // ==========================================
    @GetMapping({"", "/", "/dashboard"})
    public String dashboard(Model model, HttpSession session) {
        nxvUser admin = getAdminUser(session);
        if (admin == null) return "redirect:/login";

        model.addAttribute("nxvUser", admin);
        model.addAttribute("stats", adminService.getDashboardStats());
        model.addAttribute("topUsers", adminService.getTopDepositors());
        model.addAttribute("boxes", adminService.getAllBoxes()); // Short list for quick management
        model.addAttribute("listCategories", adminService.getAllCategories()); // For "Add Box" modal
        model.addAttribute("users", userRepository.findAll()); // For "Add Money" modal

        return "admin/dashboard";
    }

    // ==========================================
    // 2. ORDER MANAGEMENT & SHIPMENT APPROVAL
    // ==========================================
    @GetMapping("/orders") // Matches Sidebar link /admin/orders
    public String viewOrders(Model model, HttpSession session) {
        nxvUser admin = getAdminUser(session);
        if (admin == null) return "redirect:/login";

        // Fetch all orders from Service
        model.addAttribute("listOrders", adminService.getAllOrders());
        model.addAttribute("nxvUser", admin);
        return "admin/orders"; // Points to orders.html
    }

    // API to handle order status updates (Called via AJAX)
    @PostMapping("/shipment/update-status")
    @ResponseBody
    public ResponseEntity<?> updateShipmentStatus(@RequestParam Integer orderId, @RequestParam String status, HttpSession session) {
        if (getAdminUser(session) == null) return ResponseEntity.status(401).body("Unauthorized");

        // Call service to update status (PENDING -> SHIPPING -> DELIVERED)
        adminService.updateOrderStatus(orderId, status);

        return ResponseEntity.ok("Status updated successfully!");
    }

    // ==========================================
    // 3. INVENTORY & ALERTS
    // ==========================================
    @GetMapping("/inventory")
    public String viewInventory(Model model, HttpSession session) {
        nxvUser admin = getAdminUser(session);
        if (admin == null) return "redirect:/login";

        model.addAttribute("lowStockItems", adminService.getLowStockItems());
        model.addAttribute("nxvUser", admin);
        return "admin/inventory";
    }

    @PostMapping("/inventory/update")
    @ResponseBody
    public ResponseEntity<?> updateStock(@RequestParam Integer itemId, @RequestParam Integer quantity, HttpSession session) {
        if (getAdminUser(session) == null) return ResponseEntity.status(401).body("Unauthorized");
        adminService.updateStock(itemId, quantity);
        return ResponseEntity.ok("Updated");
    }

    // ==========================================
    // 4. USER MANAGEMENT & ADD MONEY
    // ==========================================
    @PostMapping("/add-money")
    public String addMoney(@RequestParam Integer userId, @RequestParam BigDecimal amount, HttpSession session) {
        if (getAdminUser(session) == null) return "redirect:/login";
        adminService.addMoneyToUser(userId, amount);
        return "redirect:/admin"; // Return to dashboard
    }

    // ==========================================
    // 5. BLIND BOX MANAGEMENT
    // ==========================================
    @PostMapping("/save-box")
    public String saveBox(@ModelAttribute nxvBlindBox box, HttpSession session) {
        if (getAdminUser(session) == null) return "redirect:/login";
        adminService.saveBox(box);
        return "redirect:/admin";
    }

    @GetMapping("/delete-box/{id}")
    public String deleteBox(@PathVariable Integer id, HttpSession session) {
        if (getAdminUser(session) == null) return "redirect:/login";
        try { adminService.deleteBox(id); } catch (Exception e) {}
        return "redirect:/admin";
    }

    // ==========================================
    // 6. CATEGORY MANAGEMENT
    // ==========================================
    @GetMapping("/categories")
    public String viewCategories(Model model, HttpSession session) {
        nxvUser admin = getAdminUser(session);
        if (admin == null) return "redirect:/login";
        model.addAttribute("listCategories", adminService.getAllCategories());
        model.addAttribute("newCategory", new nxvCategory());
        model.addAttribute("nxvUser", admin);
        return "admin/categories";
    }

    @PostMapping("/categories/save")
    public String saveCategory(@ModelAttribute nxvCategory category, HttpSession session) {
        if (getAdminUser(session) == null) return "redirect:/login";
        adminService.saveCategory(category);
        return "redirect:/admin/categories";
    }

    @GetMapping("/categories/delete/{id}")
    public String deleteCategory(@PathVariable Integer id, HttpSession session) {
        if (getAdminUser(session) == null) return "redirect:/login";
        adminService.deleteCategory(id);
        return "redirect:/admin/categories";
    }

    // ==========================================
    // 7. NEWS MANAGEMENT
    // ==========================================
    @GetMapping("/news")
    public String viewNews(Model model, HttpSession session) {
        nxvUser admin = getAdminUser(session);
        if (admin == null) return "redirect:/login";
        model.addAttribute("listNews", adminService.getAllNews());
        model.addAttribute("newNews", new nxvNews());
        model.addAttribute("nxvUser", admin);
        return "admin/news";
    }

    @PostMapping("/news/save")
    public String saveNews(@ModelAttribute nxvNews news, HttpSession session) {
        if (getAdminUser(session) == null) return "redirect:/login";
        adminService.saveNews(news);
        return "redirect:/admin/news";
    }

    @GetMapping("/news/delete/{id}")
    public String deleteNews(@PathVariable Integer id, HttpSession session) {
        if (getAdminUser(session) == null) return "redirect:/login";
        adminService.deleteNews(id);
        return "redirect:/admin/news";
    }

    // ==========================================
    // 8. BANNER MANAGEMENT
    // ==========================================
    @GetMapping("/banners")
    public String viewBanners(Model model, HttpSession session) {
        nxvUser admin = getAdminUser(session);
        if (admin == null) return "redirect:/login";
        model.addAttribute("listBanners", adminService.getAllBanners());
        model.addAttribute("newBanner", new nxvBanner());
        model.addAttribute("nxvUser", admin);
        return "admin/banners";
    }

    @PostMapping("/banners/save")
    public String saveBanner(@ModelAttribute nxvBanner banner, HttpSession session) {
        if (getAdminUser(session) == null) return "redirect:/login";
        adminService.saveBanner(banner);
        return "redirect:/admin/banners";
    }

    @GetMapping("/banners/delete/{id}")
    public String deleteBanner(@PathVariable Integer id, HttpSession session) {
        if (getAdminUser(session) == null) return "redirect:/login";
        adminService.deleteBanner(id);
        return "redirect:/admin/banners";
    }

    // ==========================================
    // 9. FEEDBACK MANAGEMENT
    // ==========================================
    @GetMapping("/feedbacks")
    public String viewFeedbacks(Model model, HttpSession session) {
        nxvUser admin = getAdminUser(session);
        if (admin == null) return "redirect:/login";
        model.addAttribute("listFeedbacks", adminService.getAllFeedbacks());
        model.addAttribute("nxvUser", admin);
        return "admin/feedbacks";
    }

    @PostMapping("/feedbacks/reply")
    public String replyFeedback(@RequestParam Integer id, @RequestParam String replyContent, HttpSession session) {
        if (getAdminUser(session) == null) return "redirect:/login";
        adminService.replyFeedback(id, replyContent);
        return "redirect:/admin/feedbacks";
    }
}