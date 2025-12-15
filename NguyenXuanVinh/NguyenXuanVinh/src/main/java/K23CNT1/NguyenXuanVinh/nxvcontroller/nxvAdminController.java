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

    // Helper: Check quyền Admin từ Session
    private nxvUser getAdminUser(HttpSession session) {
        Object sessionUser = session.getAttribute("currentUser");
        if (sessionUser instanceof nxvUser) {
            nxvUser user = (nxvUser) sessionUser;
            // Chỉ trả về user nếu là Admin (IsAdmin = true)
            if (Boolean.TRUE.equals(user.getIsAdmin())) return user;
        }
        return null;
    }

    // --- 1. DASHBOARD CHÍNH ---
    // URL: /admin hoặc /admin/dashboard
    @GetMapping({"", "/", "/dashboard"})
    public String dashboard(Model model, HttpSession session) {
        nxvUser admin = getAdminUser(session);
        if (admin == null) return "redirect:/login"; // Đá về login nếu không phải admin

        // Đẩy dữ liệu thống kê ra Dashboard
        model.addAttribute("nxvUser", admin);
        model.addAttribute("stats", adminService.getDashboardStats()); // Tổng tiền, user, đơn hàng...
        model.addAttribute("topUsers", adminService.getTopDepositors()); // Bảng xếp hạng nạp tiền
        model.addAttribute("boxes", adminService.getAllBoxes()); // List hộp để quản lý nhanh
        model.addAttribute("listCategories", adminService.getAllCategories());

        return "admin/dashboard"; // Trỏ về file dashboard.html
    }

    // --- 2. QUẢN LÝ NGƯỜI DÙNG (USERS) ---
    @GetMapping("/users")
    public String viewUsers(Model model, HttpSession session) {
        nxvUser admin = getAdminUser(session);
        if (admin == null) return "redirect:/login";

        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("nxvUser", admin);
        return "admin/users"; // Tạo file users.html nếu chưa có
    }

    @PostMapping("/add-money")
    public String addMoney(@RequestParam Integer userId, @RequestParam BigDecimal amount, HttpSession session) {
        if (getAdminUser(session) == null) return "redirect:/login";
        adminService.addMoneyToUser(userId, amount);
        return "redirect:/admin/users"; // Reload trang users
    }

    // --- 3. QUẢN LÝ BLIND BOX (SẢN PHẨM) ---
    @GetMapping("/blindbox")
    public String viewBlindBoxes(Model model, HttpSession session) {
        nxvUser admin = getAdminUser(session);
        if (admin == null) return "redirect:/login";

        model.addAttribute("boxes", adminService.getAllBoxes());
        model.addAttribute("listCategories", adminService.getAllCategories());
        model.addAttribute("newBox", new nxvBlindBox()); // Để form thêm mới
        model.addAttribute("nxvUser", admin);
        return "admin/blindbox"; // Tạo file blindbox.html
    }

    @PostMapping("/save-box")
    public String saveBox(@ModelAttribute nxvBlindBox box, HttpSession session) {
        if (getAdminUser(session) == null) return "redirect:/login";
        adminService.saveBox(box);
        return "redirect:/admin/blindbox";
    }

    @GetMapping("/delete-box/{id}")
    public String deleteBox(@PathVariable Integer id, HttpSession session) {
        if (getAdminUser(session) == null) return "redirect:/login";
        try { adminService.deleteBox(id); } catch (Exception e) {}
        return "redirect:/admin/blindbox";
    }

    // --- 4. QUẢN LÝ TIN TỨC (NEWS) ---
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

    // --- 5. QUẢN LÝ VẬN CHUYỂN (SHIPMENT) ---
    // Cái này quan trọng để duyệt đơn ship của user
    @GetMapping("/shipment")
    public String viewShipments(Model model, HttpSession session) {
        nxvUser admin = getAdminUser(session);
        if (admin == null) return "redirect:/login";

        model.addAttribute("shipmentRequests", adminService.getAllOrders()); // Lấy list đơn
        model.addAttribute("nxvUser", admin);
        return "admin/shipment"; // Tạo file shipment.html
    }

    @PostMapping("/shipment/update-status")
    @ResponseBody // Trả về JSON để JS xử lý (không reload trang)
    public ResponseEntity<?> updateShipmentStatus(@RequestParam Integer orderId, @RequestParam String status, HttpSession session) {
        if (getAdminUser(session) == null) return ResponseEntity.status(401).body("Unauthorized");
        adminService.updateOrderStatus(orderId, status);
        return ResponseEntity.ok("Cập nhật trạng thái thành công!");
    }

    // --- 6. CÁC DANH MỤC KHÁC (Categories, Banners...) ---
    // (Giữ nguyên logic cũ nếu cần, hoặc gom gọn vào dashboard)
}