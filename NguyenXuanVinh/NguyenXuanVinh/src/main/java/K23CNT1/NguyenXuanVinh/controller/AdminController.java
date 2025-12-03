package K23CNT1.NguyenXuanVinh.controller;

import K23CNT1.NguyenXuanVinh.entity.BlindBox;
import K23CNT1.NguyenXuanVinh.entity.User;
import K23CNT1.NguyenXuanVinh.repository.UserRepository;
import K23CNT1.NguyenXuanVinh.service.AdminService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor // Inject Service tự động
public class AdminController {

    private final AdminService adminService;
    private final UserRepository userRepository; // Chỉ dùng để lấy list user cho dropdown

    // Middleware kiểm tra quyền (Private)
    private boolean isNotAdmin(HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        return user == null || !Boolean.TRUE.equals(user.getIsAdmin());
    }

    @GetMapping("")
    public String dashboard(Model model, HttpSession session) {
        if (isNotAdmin(session)) return "redirect:/login";

        // Code gọn hơ: Gọi Service lấy data và đẩy ra View
        model.addAttribute("stats", adminService.getDashboardStats()); // DTO thống kê
        model.addAttribute("topUsers", adminService.getTopDepositors());
        model.addAttribute("boxes", adminService.getAllBoxes());
        model.addAttribute("users", userRepository.findAll()); // Để hiện dropdown chọn người bơm tiền
        model.addAttribute("user", session.getAttribute("currentUser"));

        return "admin/dashboard";
    }

    @PostMapping("/add-money")
    public String addMoney(@RequestParam Integer userId, @RequestParam BigDecimal amount, HttpSession session) {
        if (isNotAdmin(session)) return "redirect:/login";

        adminService.addMoneyToUser(userId, amount);
        return "redirect:/admin";
    }

    @PostMapping("/save-box")
    public String saveBox(@ModelAttribute BlindBox box, HttpSession session) {
        if (isNotAdmin(session)) return "redirect:/login";

        adminService.saveBox(box);
        return "redirect:/admin";
    }

    @GetMapping("/delete-box/{id}")
    public String deleteBox(@PathVariable Integer id, HttpSession session) {
        if (isNotAdmin(session)) return "redirect:/login";

        try {
            adminService.deleteBox(id);
        } catch (Exception e) {
            // Xử lý lỗi ràng buộc khóa ngoại (nếu cần)
        }
        return "redirect:/admin";
    }
}