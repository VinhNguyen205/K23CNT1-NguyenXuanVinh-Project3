package K23CNT1.NguyenXuanVinh.nxvcontroller;

import K23CNT1.NguyenXuanVinh.nxventity.nxvBlindBox;
import K23CNT1.NguyenXuanVinh.nxventity.nxvUser;
import K23CNT1.NguyenXuanVinh.nxvrepository.nxvUserRepository;
import K23CNT1.NguyenXuanVinh.nxvservice.nxvAdminService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
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

    // Helper: Lấy admin user từ session
    private nxvUser getAdminUser(HttpSession session) {
        Object sessionUser = session.getAttribute("currentUser");
        if (sessionUser instanceof nxvUser) {
            nxvUser user = (nxvUser) sessionUser;
            if (Boolean.TRUE.equals(user.getIsAdmin())) {
                return user;
            }
        }
        return null;
    }

    @GetMapping("")
    public String dashboard(Model model, HttpSession session) {
        // 1. Check quyền
        nxvUser admin = getAdminUser(session);
        if (admin == null) return "redirect:/login";

        // 2. Đẩy dữ liệu ra View với tên biến là "nxvUser" (FIX LỖI TẠI ĐÂY)
        model.addAttribute("nxvUser", admin);

        // 3. Các dữ liệu thống kê
        model.addAttribute("stats", adminService.getDashboardStats());
        model.addAttribute("topUsers", adminService.getTopDepositors());
        model.addAttribute("boxes", adminService.getAllBoxes());
        model.addAttribute("users", userRepository.findAll());

        return "admin/dashboard";
    }

    @PostMapping("/add-money")
    public String addMoney(@RequestParam Integer userId, @RequestParam BigDecimal amount, HttpSession session) {
        if (getAdminUser(session) == null) return "redirect:/login";
        adminService.addMoneyToUser(userId, amount);
        return "redirect:/admin";
    }

    @PostMapping("/save-box")
    public String saveBox(@ModelAttribute nxvBlindBox box, HttpSession session) {
        if (getAdminUser(session) == null) return "redirect:/login";
        adminService.saveBox(box);
        return "redirect:/admin";
    }

    @GetMapping("/delete-box/{id}")
    public String deleteBox(@PathVariable Integer id, HttpSession session) {
        if (getAdminUser(session) == null) return "redirect:/login";
        try {
            adminService.deleteBox(id);
        } catch (Exception e) {
            // Xử lý lỗi nếu cần
        }
        return "redirect:/admin";
    }
}