package K23CNT1.NguyenXuanVinh.nxvcontroller;

// Đã đổi tên các file phụ thuộc
import K23CNT1.NguyenXuanVinh.nxvdto.nxvLoginRequest;
import K23CNT1.NguyenXuanVinh.nxvdto.nxvRegisterRequest;
import K23CNT1.NguyenXuanVinh.nxventity.nxvUser;
import K23CNT1.NguyenXuanVinh.nxvrepository.nxvUserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Controller
public class nxvAuthController { // Đã đổi tên Class

    @Autowired
    private nxvUserRepository nxvUserRepository; // Đã đổi tên Type và variable

    // ============================================================
    // PHẦN 1: ĐIỀU HƯỚNG TRANG (VIEW)
    // ============================================================

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @GetMapping("/register")
    public String showRegisterForm() {
        return "register";
    }

    // ============================================================
    // PHẦN 2: XỬ LÝ API (LOGIC)
    // ============================================================

    /**
     * API Đăng Nhập
     */
    @PostMapping("/api/auth/login")
    @ResponseBody
    public ResponseEntity<?> login(@RequestBody nxvLoginRequest request, HttpSession session) { // DTO đã đổi tên
        // 1. Tìm user trong DB
        nxvUser user = nxvUserRepository.findByUsername(request.getUsername()).orElse(null); // Type và Repository đã đổi

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
     */
    @PostMapping("/api/auth/register")
    @ResponseBody
    public ResponseEntity<?> register(@RequestBody nxvRegisterRequest request) { // DTO đã đổi tên
        // 1. Kiểm tra trùng tên đăng nhập
        if (nxvUserRepository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Tên đăng nhập đã tồn tại!");
        }

        // 2. Tạo user mới
        nxvUser newUser = new nxvUser(); // Type đã đổi tên
        newUser.setUsername(request.getUsername());
        newUser.setPasswordHash(request.getPassword());
        newUser.setFullName(request.getFullName());
        newUser.setEmail(request.getEmail());
        newUser.setWalletBalance(BigDecimal.ZERO);
        newUser.setIsAdmin(false);

        nxvUserRepository.save(newUser); // Repository đã đổi tên

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
        session.invalidate();
        return "redirect:/login";
    }
}