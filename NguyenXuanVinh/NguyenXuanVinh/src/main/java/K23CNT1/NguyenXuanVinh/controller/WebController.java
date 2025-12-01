package K23CNT1.NguyenXuanVinh.controller;

import K23CNT1.NguyenXuanVinh.repository.BlindBoxRepository;
import K23CNT1.NguyenXuanVinh.repository.UserInventoryRepository;
import K23CNT1.NguyenXuanVinh.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller // Lưu ý: Dùng @Controller, KHÔNG dùng @RestController
public class WebController {

    @Autowired private BlindBoxRepository blindBoxRepository;
    @Autowired private UserRepository userRepository;

    // 1. Trang chủ: Hiển thị danh sách Hộp Blind Box
    @GetMapping("/")
    public String home(Model model) {
        // Lấy danh sách tất cả các hộp từ DB
        model.addAttribute("boxes", blindBoxRepository.findAll());

        // Lấy thông tin user (tạm thời lấy cứng user ID = 1)
        model.addAttribute("user", userRepository.findById(1).orElse(null));

        return "index"; // Trả về file index.html
    }
}