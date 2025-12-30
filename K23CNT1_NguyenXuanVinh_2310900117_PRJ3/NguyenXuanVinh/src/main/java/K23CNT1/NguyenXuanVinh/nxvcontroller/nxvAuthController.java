package K23CNT1.NguyenXuanVinh.nxvcontroller;

import K23CNT1.NguyenXuanVinh.nxventity.nxvUser;
import K23CNT1.NguyenXuanVinh.nxvrepository.nxvUserRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Controller
@RequiredArgsConstructor // Tự động Inject Repository (Thay cho @Autowired thủ công)
public class nxvAuthController {

    private final nxvUserRepository nxvUserRepository;

    // ============================================================
    // PHẦN 1: HIỂN THỊ TRANG (VIEW)
    // ============================================================

    @GetMapping("/login")
    public String showLoginForm() {
        return "login"; // Trả về file login.html
    }

    @GetMapping("/register")
    public String showRegisterForm() {
        return "register"; // Trả về file register.html
    }

    // ============================================================
    // PHẦN 2: XỬ LÝ FORM SUBMIT (LOGIC)
    // ============================================================

    /**
     * Xử lý Đăng Nhập (Form Submit)
     */
    @PostMapping("/login")
    public String handleLogin(@RequestParam String username,
                              @RequestParam String password,
                              HttpSession session,
                              Model model) {

        // 1. Tìm user trong DB
        nxvUser user = nxvUserRepository.findByUsername(username).orElse(null);

        // 2. Kiểm tra mật khẩu (So sánh chuỗi thường, thực tế nên mã hóa)
        if (user != null && user.getPasswordHash().equals(password)) {
            // Lưu User vào Session để dùng xuyên suốt
            session.setAttribute("currentUser", user);

            // Phân quyền điều hướng
            if (Boolean.TRUE.equals(user.getIsAdmin())) {
                return "redirect:/admin"; // Admin vào trang quản trị
            } else {
                return "redirect:/"; // User thường về trang chủ
            }
        }

        // 3. Đăng nhập thất bại -> Trả về trang login kèm thông báo lỗi
        model.addAttribute("error", "Sai tên đăng nhập hoặc mật khẩu!");
        return "login";
    }

    /**
     * Xử lý Đăng Ký (Form Submit)
     */
    @PostMapping("/register")
    public String handleRegister(@RequestParam String username,
                                 @RequestParam String password,
                                 @RequestParam String confirmPassword,
                                 @RequestParam String fullName,
                                 @RequestParam String email,
                                 Model model) {

        // 1. Kiểm tra mật khẩu nhập lại
        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Mật khẩu nhập lại không khớp!");
            return "register";
        }

        // 2. Kiểm tra trùng tên đăng nhập
        if (nxvUserRepository.findByUsername(username).isPresent()) {
            model.addAttribute("error", "Tên đăng nhập đã tồn tại!");
            return "register";
        }

        // 3. Tạo User mới
        try {
            nxvUser newUser = new nxvUser();
            newUser.setUsername(username);
            newUser.setPasswordHash(password); // Lưu ý: Chưa mã hóa (cho project học tập)
            newUser.setFullName(fullName);
            newUser.setEmail(email);
            newUser.setWalletBalance(BigDecimal.ZERO); // Ví 0 đồng
            newUser.setIsAdmin(false); // Mặc định là User thường

            nxvUserRepository.save(newUser);

            // Đăng ký thành công -> Chuyển sang trang Login
            return "redirect:/login?success";

        } catch (Exception e) {
            model.addAttribute("error", "Lỗi hệ thống: " + e.getMessage());
            return "register";
        }
    }

    // ============================================================
    // PHẦN 3: ĐĂNG XUẤT
    // ============================================================

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // Xóa sạch session
        return "redirect:/"; // Về trang chủ
    }
}