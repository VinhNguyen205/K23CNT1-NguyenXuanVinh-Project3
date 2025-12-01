package K23CNT1.NguyenXuanVinh.service.impl;

import K23CNT1.NguyenXuanVinh.dto.OpenBoxResponse;
import K23CNT1.NguyenXuanVinh.dto.SellItemResponse;
import K23CNT1.NguyenXuanVinh.entity.*;
import K23CNT1.NguyenXuanVinh.repository.*;
import K23CNT1.NguyenXuanVinh.service.BlindBoxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

@Service
public class BlindBoxServiceImpl implements BlindBoxService {

    @Autowired private UserRepository userRepository;
    @Autowired private BlindBoxRepository blindBoxRepository;
    @Autowired private BoxItemRepository boxItemRepository;
    @Autowired private UserInventoryRepository userInventoryRepository;
    @Autowired private TransactionRepository transactionRepository;
    @Autowired private UserPityStatRepository userPityStatRepository;

    private final Random random = new Random();

    // ==========================================================
    // CHỨC NĂNG 1: MUA VÀ MỞ HỘP (BUY & OPEN)
    // ==========================================================
    @Override
    @Transactional
    public OpenBoxResponse buyAndOpenBox(Integer userId, Integer boxId) {
        // 1. Kiểm tra User và Hộp
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại!"));
        BlindBox box = blindBoxRepository.findById(boxId)
                .orElseThrow(() -> new RuntimeException("Hộp này không tồn tại!"));

        // 2. Kiểm tra tiền
        if (user.getWalletBalance().compareTo(box.getPrice()) < 0) {
            throw new RuntimeException("Bạn không đủ tiền! Vui lòng nạp thêm.");
        }

        // 3. Trừ tiền
        user.setWalletBalance(user.getWalletBalance().subtract(box.getPrice()));
        userRepository.save(user);

        // 4. Ghi lịch sử mua
        Transaction trans = new Transaction();
        trans.setUser(user);
        trans.setAmount(box.getPrice().negate());
        trans.setTransactionType("BUY_BOX");
        trans.setDescription("Mua hộp: " + box.getBoxName());
        transactionRepository.save(trans);

        // 5. Lấy danh sách item trong hộp
        List<BoxItem> items = boxItemRepository.findByBlindBox(box);
        if (items.isEmpty()) throw new RuntimeException("Hộp này rỗng!");

        // 6. Xử lý Bảo Hiểm (Pity System)
        UserPityStat pityStat = userPityStatRepository.findByUserAndBlindBox(user, box)
                .orElseGet(() -> {
                    UserPityStat newStat = new UserPityStat();
                    newStat.setUser(user);
                    newStat.setBlindBox(box);
                    newStat.setSpinsWithoutS(0);
                    return userPityStatRepository.save(newStat);
                });

        BoxItem selectedItem;
        boolean isPity = false;

        // Nếu đen 50 lần -> Chắc chắn ra S
        if (pityStat.getSpinsWithoutS() >= 50) {
            selectedItem = items.stream()
                    .filter(i -> "S".equals(i.getRarityLevel()))
                    .findFirst()
                    .orElse(items.get(0));
            isPity = true;
            pityStat.setSpinsWithoutS(0); // Reset bảo hiểm
        } else {
            // Random bình thường
            selectedItem = performRandomDrop(items);

            // Cập nhật bảo hiểm
            if ("S".equals(selectedItem.getRarityLevel())) {
                pityStat.setSpinsWithoutS(0);
            } else {
                pityStat.setSpinsWithoutS(pityStat.getSpinsWithoutS() + 1);
            }
        }
        userPityStatRepository.save(pityStat);

        // 7. Lưu vào kho (UserInventory)
        UserInventory inventory = new UserInventory();
        inventory.setUser(user);
        inventory.setBoxItem(selectedItem);
        inventory.setStatus("IN_STORAGE");
        userInventoryRepository.save(inventory);

        // 8. Trừ tồn kho (Stock)
        if (selectedItem.getStockQuantity() > 0) {
            selectedItem.setStockQuantity(selectedItem.getStockQuantity() - 1);
            boxItemRepository.save(selectedItem);
        }

        // 9. Trả về kết quả
        OpenBoxResponse response = new OpenBoxResponse();
        response.setItemName(selectedItem.getItemName());
        response.setItemImage(selectedItem.getImageUrl());
        response.setRarity(selectedItem.getRarityLevel());
        response.setCurrentBalance(user.getWalletBalance());
        response.setPityTriggered(isPity);
        response.setInventoryItem(inventory);

        return response;
    }

    // ==========================================================
    // CHỨC NĂNG 2: BÁN LẠI VẬT PHẨM (SELL / BUYBACK)
    // ==========================================================
    @Override
    @Transactional
    public SellItemResponse sellItem(Integer userId, Integer inventoryId) {
        // 1. Kiểm tra User
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại!"));

        // 2. Tìm vật phẩm trong kho
        UserInventory inventory = userInventoryRepository.findByInventoryIdAndUser(inventoryId, user)
                .orElseThrow(() -> new RuntimeException("Vật phẩm không tồn tại hoặc không chính chủ!"));

        // 3. Kiểm tra trạng thái
        if (!"IN_STORAGE".equals(inventory.getStatus())) {
            throw new RuntimeException("Vật phẩm này đã bán hoặc đang ship, không thể bán lại!");
        }

        // 4. Tính giá bán (90% giá trị thực)
        BigDecimal marketValue = inventory.getBoxItem().getMarketValue();
        if (marketValue == null) marketValue = BigDecimal.ZERO;

        BigDecimal sellPrice = marketValue.multiply(new BigDecimal("0.9"));

        // 5. Cộng tiền
        user.setWalletBalance(user.getWalletBalance().add(sellPrice));
        userRepository.save(user);

        // 6. Cập nhật trạng thái kho -> SOLD_BACK
        inventory.setStatus("SOLD_BACK");
        inventory.setSoldPrice(sellPrice);
        userInventoryRepository.save(inventory);

        // 7. Ghi lịch sử bán
        Transaction trans = new Transaction();
        trans.setUser(user);
        trans.setAmount(sellPrice);
        trans.setTransactionType("SELL_BACK");
        trans.setDescription("Bán lại: " + inventory.getBoxItem().getItemName());
        transactionRepository.save(trans);

        // 8. Trả về kết quả
        SellItemResponse response = new SellItemResponse();
        response.setItemName(inventory.getBoxItem().getItemName());
        response.setSoldPrice(sellPrice);
        response.setNewBalance(user.getWalletBalance());
        response.setMessage("Bán thành công! Bạn nhận được " + sellPrice + " VNĐ");

        return response;
    }

    // --- Hàm phụ: Random theo tỉ lệ ---
    private BoxItem performRandomDrop(List<BoxItem> items) {
        double totalWeight = 0.0;
        for (BoxItem item : items) {
            totalWeight += (item.getProbability() != null) ? item.getProbability() : 0;
        }

        double randomValue = random.nextDouble() * totalWeight;
        double currentSum = 0.0;

        for (BoxItem item : items) {
            currentSum += (item.getProbability() != null) ? item.getProbability() : 0;
            if (randomValue <= currentSum) {
                return item;
            }
        }
        return items.get(items.size() - 1);
    }
}