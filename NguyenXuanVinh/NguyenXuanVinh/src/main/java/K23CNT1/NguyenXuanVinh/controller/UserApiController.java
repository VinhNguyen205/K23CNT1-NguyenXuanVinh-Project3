package K23CNT1.NguyenXuanVinh.controller;

import K23CNT1.NguyenXuanVinh.entity.User;
import K23CNT1.NguyenXuanVinh.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserApiController {

    @Autowired private UserRepository userRepository;

    // API Cập nhật thông tin cá nhân
    @PostMapping("/update")
    public ResponseEntity<?> updateProfile(@RequestParam Integer userId,
                                           @RequestParam String fullName,
                                           @RequestParam String email,
                                           @RequestParam String address,
                                           @RequestParam String phone,
                                           HttpSession session) {
        try {
            User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

            // Cập nhật thông tin
            user.setFullName(fullName);
            user.setEmail(email);
            // Lưu ý: Cần thêm cột Address và Phone vào Entity User nếu chưa có
            // Tạm thời mình giả định bạn chưa có, nên mình sẽ chỉ update FullName và Email trước
            // Để làm chuẩn, tí nữa mình sẽ hướng dẫn thêm cột vào DB sau.

            userRepository.save(user);

            // Cập nhật lại session
            session.setAttribute("currentUser", user);

            return ResponseEntity.ok("Cập nhật thành công!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }
}