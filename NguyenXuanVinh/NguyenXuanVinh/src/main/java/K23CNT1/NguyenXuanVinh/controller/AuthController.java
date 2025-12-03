package K23CNT1.NguyenXuanVinh.controller;

import K23CNT1.NguyenXuanVinh.dto.LoginRequest;
import K23CNT1.NguyenXuanVinh.dto.RegisterRequest;
import K23CNT1.NguyenXuanVinh.entity.User;
import K23CNT1.NguyenXuanVinh.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Controller
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    // ============================================================
    // PHẦN 1: ĐIỀU HƯỚNG TRANG (VIEW)
    // ============================================================

    @GetMapping("/login")
    public String showLoginForm() {
        return "login"; // Trả về templates/login.html
    }

    @GetMapping("/register")
    public String showRegisterForm() {
        return "register"; // Trả về templates/register.html
    }

    // ============================================================
    // PHẦN 2: XỬ LÝ API (LOGIC)
    // ============================================================

    /**
     * API Đăng Nhập
     * Trả về JSON: { "message": "...", "role": "ADMIN/USER" }
     */
    @PostMapping("/api/auth/login")
    @ResponseBody
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpSession session) {
        // 1. Tìm user trong DB
        User user = userRepository.findByUsername(request.getUsername()).orElse(null);

        // 2. Kiểm tra mật khẩu (So sánh chuỗi thường)
        if (user != null && user.getPasswordHash().equals(request.getPassword())) {
            // Lưu vào Session
            session.setAttribute("currentUser", user);

            // Xác định quyền (Role)
            String role = (user.getIsAdmin() != null && user.getIsAdmin()) ? "ADMIN" : "USER";

            // Tạo phản hồi JSON chuẩn bằng Map
            Map<String, String> response = new HashMap<>();
            response.put("message", "Đăng nhập thành công!");
            response.put("role", role);

            return ResponseEntity.ok(response);
        }

        // Đăng nhập thất bại
        return ResponseEntity.badRequest().body("Sai tài khoản hoặc mật khẩu!");
    }

    /**
     * API Đăng Ký
     * Trả về JSON: { "message": "..." }
     */
    @PostMapping("/api/auth/register")
    @ResponseBody
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        // 1. Kiểm tra trùng tên đăng nhập
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Tên đăng nhập đã tồn tại!");
        }

        // 2. Tạo user mới
        User newUser = new User();
        newUser.setUsername(request.getUsername());
        newUser.setPasswordHash(request.getPassword()); // Lưu pass thường
        newUser.setFullName(request.getFullName());
        newUser.setEmail(request.getEmail());
        newUser.setWalletBalance(BigDecimal.ZERO); // Ví 0 đồng
        newUser.setIsAdmin(false); // Mặc định là User thường

        userRepository.save(newUser);

        // 3. Trả về kết quả JSON
        Map<String, String> response = new HashMap<>();
        response.put("message", "Đăng ký thành công! Hãy đăng nhập ngay.");

        return ResponseEntity.ok(response);
    }

    // ============================================================
    // PHẦN 3: ĐĂNG XUẤT
    // ============================================================

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // Xóa sạch session
        return "redirect:/login"; // Đá về trang login
    }
}