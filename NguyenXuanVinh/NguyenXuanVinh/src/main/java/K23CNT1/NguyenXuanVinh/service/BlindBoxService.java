package K23CNT1.NguyenXuanVinh.service;

import K23CNT1.NguyenXuanVinh.dto.OpenBoxResponse;
import K23CNT1.NguyenXuanVinh.dto.SellItemResponse; // <-- Nhớ Import dòng này

public interface BlindBoxService {

    // Hàm cũ
    OpenBoxResponse buyAndOpenBox(Integer userId, Integer boxId);

    // --> BẠN ĐANG THIẾU DÒNG NÀY, THÊM VÀO LÀ HẾT LỖI:
    SellItemResponse sellItem(Integer userId, Integer inventoryId);
}