package K23CNT1.NguyenXuanVinh.controller;

import K23CNT1.NguyenXuanVinh.entity.BlindBox;
import K23CNT1.NguyenXuanVinh.entity.User;
import K23CNT1.NguyenXuanVinh.repository.BlindBoxRepository;
import K23CNT1.NguyenXuanVinh.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController // Báo hiệu đây là API trả về dữ liệu (JSON)
@RequestMapping("/api/test") // Đường dẫn gốc
public class TestConnectionController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BlindBoxRepository blindBoxRepository;

    // Test 1: Lấy danh sách User
    // Truy cập: http://localhost:8080/api/test/users
    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Test 2: Lấy danh sách Hộp Blind Box
    // Truy cập: http://localhost:8080/api/test/boxes
    @GetMapping("/boxes")
    public List<BlindBox> getAllBoxes() {
        return blindBoxRepository.findAll();
    }
}