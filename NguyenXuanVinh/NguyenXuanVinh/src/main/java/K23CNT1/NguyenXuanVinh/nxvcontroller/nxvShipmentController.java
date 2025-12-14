package K23CNT1.NguyenXuanVinh.nxvcontroller;

import K23CNT1.NguyenXuanVinh.nxventity.*;
import K23CNT1.NguyenXuanVinh.nxvrepository.nxvUserInventoryRepository;
import K23CNT1.NguyenXuanVinh.nxvservice.nxvShipmentService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/shipment")
@RequiredArgsConstructor
public class nxvShipmentController {

    private final nxvShipmentService shipmentService;
    private final nxvUserInventoryRepository inventoryRepository;

    // Helper lấy user
    private nxvUser getCurrentUser(HttpSession session) {
        return (nxvUser) session.getAttribute("currentUser");
    }

    // 1. Trang Tạo Yêu Cầu Ship
    @GetMapping("/create")
    public String createShipmentPage(Model model, HttpSession session) {
        nxvUser user = getCurrentUser(session);
        if (user == null) return "redirect:/login";

        // Chỉ lấy những món đang "IN_STORAGE"
        List<nxvUserInventory> items = inventoryRepository.findByUserAndStatus(user, "IN_STORAGE");

        model.addAttribute("items", items);
        model.addAttribute("nxvUser", user);
        return "shipment/create";
    }

    // 2. Xử lý Form Ship
    @PostMapping("/submit")
    public String submitShipment(@RequestParam String receiverName,
                                 @RequestParam String phoneNumber,
                                 @RequestParam String address,
                                 @RequestParam(required = false) String note,
                                 @RequestParam(required = false) List<Integer> selectedItems,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {

        nxvUser user = getCurrentUser(session);
        if (user == null) return "redirect:/login";

        if (selectedItems == null || selectedItems.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng chọn ít nhất 1 món đồ!");
            return "redirect:/shipment/create";
        }

        try {
            shipmentService.createShipmentRequest(user.getUserId(), receiverName, phoneNumber, address, note, selectedItems);
            redirectAttributes.addFlashAttribute("success", "Yêu cầu ship thành công! Phí 20k đã được trừ.");
            return "redirect:/shipment/history";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/shipment/create";
        }
    }

    // 3. Trang Lịch Sử / Trạng Thái
    @GetMapping("/history")
    public String shipmentHistory(Model model, HttpSession session) {
        nxvUser user = getCurrentUser(session);
        if (user == null) return "redirect:/login";

        model.addAttribute("requests", shipmentService.getUserHistory(user));
        model.addAttribute("nxvUser", user);
        return "shipment/history";
    }
}