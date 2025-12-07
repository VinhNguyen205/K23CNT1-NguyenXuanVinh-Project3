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
import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class nxvAdminController {

    private final nxvAdminService adminService;
    private final nxvUserRepository userRepository;

    // Helper: Check quyền
    private nxvUser getAdminUser(HttpSession session) {
        Object sessionUser = session.getAttribute("currentUser");
        if (sessionUser instanceof nxvUser) {
            nxvUser user = (nxvUser) sessionUser;
            if (Boolean.TRUE.equals(user.getIsAdmin())) return user;
        }
        return null;
    }

    // --- 1. DASHBOARD ---
    @GetMapping("")
    public String dashboard(Model model, HttpSession session) {
        nxvUser admin = getAdminUser(session);
        if (admin == null) return "redirect:/login";

        model.addAttribute("nxvUser", admin);
        model.addAttribute("stats", adminService.getDashboardStats());
        model.addAttribute("topUsers", adminService.getTopDepositors());
        model.addAttribute("boxes", adminService.getAllBoxes());
        model.addAttribute("listCategories", adminService.getAllCategories());
        model.addAttribute("users", userRepository.findAll());
        return "admin/dashboard";
    }

    // --- 2. USER ACTIONS ---
    @PostMapping("/add-money")
    public String addMoney(@RequestParam Integer userId, @RequestParam BigDecimal amount, HttpSession session) {
        if (getAdminUser(session) == null) return "redirect:/login";
        adminService.addMoneyToUser(userId, amount);
        return "redirect:/admin";
    }

    // --- 3. BLIND BOX ---
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

    // --- 4. ORDERS ---
    @GetMapping("/orders")
    public String viewOrders(Model model, HttpSession session) {
        nxvUser admin = getAdminUser(session);
        if (admin == null) return "redirect:/login";
        model.addAttribute("listOrders", adminService.getAllOrders());
        model.addAttribute("nxvUser", admin);
        return "admin/orders";
    }
    @PostMapping("/orders/update-status")
    @ResponseBody
    public ResponseEntity<?> updateStatus(@RequestParam Integer orderId, @RequestParam String status, HttpSession session) {
        if (getAdminUser(session) == null) return ResponseEntity.status(401).body("Unauthorized");
        adminService.updateOrderStatus(orderId, status);
        return ResponseEntity.ok("Success");
    }

    // --- 5. CATEGORIES ---
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
        try { adminService.deleteCategory(id); } catch (Exception e) {}
        return "redirect:/admin/categories";
    }

    // --- 6. NEWS ---
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
        try { adminService.deleteNews(id); } catch (Exception e) {}
        return "redirect:/admin/news";
    }

    // --- 7. QUẢN LÝ BANNERS (MỚI) ---
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

    // --- 8. PHẢN HỒI / KHIẾU NẠI (MỚI) ---
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

    // --- 9. QUẢN LÝ KHO (MỚI) ---
    @GetMapping("/inventory")
    public String viewInventory(Model model, HttpSession session) {
        nxvUser admin = getAdminUser(session);
        if (admin == null) return "redirect:/login";
        // Chỉ hiện những món sắp hết hàng (Low Stock) hoặc tất cả tùy bạn
        // Ở đây mình show list low stock để cảnh báo
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
}