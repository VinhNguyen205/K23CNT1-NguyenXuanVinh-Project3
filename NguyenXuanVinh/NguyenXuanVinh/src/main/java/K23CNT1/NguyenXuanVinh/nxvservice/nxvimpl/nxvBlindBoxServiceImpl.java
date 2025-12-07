package K23CNT1.NguyenXuanVinh.nxvservice.nxvimpl;

import K23CNT1.NguyenXuanVinh.nxvdto.nxvOpenBoxResponse;
import K23CNT1.NguyenXuanVinh.nxvdto.nxvSellItemResponse;
import K23CNT1.NguyenXuanVinh.nxventity.*;
import K23CNT1.NguyenXuanVinh.nxvrepository.*;
import K23CNT1.NguyenXuanVinh.nxvservice.nxvBlindBoxService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor // Tự động inject các biến final (Best Practice)
public class nxvBlindBoxServiceImpl implements nxvBlindBoxService {

    private final nxvUserRepository nxvUserRepository;
    private final nxvBlindBoxRepository nxvBlindBoxRepository;
    private final nxvBoxItemRepository nxvBoxItemRepository;
    private final nxvUserInventoryRepository nxvUserInventoryRepository;
    private final nxvTransactionRepository nxvTransactionRepository;
    private final nxvUserPityStatRepository nxvUserPityStatRepository;

    private final Random random = new Random();

    @Override
    @Transactional
    public nxvOpenBoxResponse buyAndOpenBox(Integer userId, Integer boxId) {
        // 1. Validate Input
        nxvUser user = nxvUserRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        nxvBlindBox box = nxvBlindBoxRepository.findById(boxId)
                .orElseThrow(() -> new RuntimeException("Box not found"));

        // 2. Check Tiền
        if (user.getWalletBalance().compareTo(box.getPrice()) < 0) {
            throw new RuntimeException("Số dư không đủ để mở hộp này!");
        }

        // 3. Trừ tiền & Lưu Transaction
        user.setWalletBalance(user.getWalletBalance().subtract(box.getPrice()));
        nxvUserRepository.save(user);

        nxvTransaction trans = new nxvTransaction();
        trans.setUser(user);
        trans.setAmount(box.getPrice().negate());
        trans.setTransactionType("BUY_BOX");
        trans.setDescription("Mua hộp: " + box.getBoxName());
        nxvTransactionRepository.save(trans);

        // 4. Lấy danh sách Item
        List<nxvBoxItem> items = nxvBoxItemRepository.findByBlindBox(box);
        if (items.isEmpty()) throw new RuntimeException("Hộp này rỗng, vui lòng liên hệ Admin!");

        // 5. Xử lý Bảo Hiểm (Pity System)
        nxvUserPityStat pityStat = nxvUserPityStatRepository.findByUserAndBlindBox(user, box)
                .orElseGet(() -> {
                    nxvUserPityStat newStat = new nxvUserPityStat();
                    newStat.setUser(user);
                    newStat.setBlindBox(box);
                    newStat.setSpinsWithoutS(0);
                    return nxvUserPityStatRepository.save(newStat);
                });

        nxvBoxItem selectedItem;
        boolean isPityTriggered = false;

        // Nếu đã quay đen đủi >= 50 lần -> Bắt buộc ra S
        if (pityStat.getSpinsWithoutS() >= 50) {
            selectedItem = items.stream()
                    .filter(i -> "S".equalsIgnoreCase(i.getRarityLevel()))
                    .findFirst()
                    .orElse(items.get(0)); // Fallback nếu hộp không có S (hiếm khi xảy ra)

            isPityTriggered = true;
            pityStat.setSpinsWithoutS(0); // Reset bảo hiểm
        } else {
            // Quay Random bình thường
            selectedItem = performRandomDrop(items);

            // Nếu may mắn ra S thì reset, không thì cộng tích lũy
            if ("S".equalsIgnoreCase(selectedItem.getRarityLevel())) {
                pityStat.setSpinsWithoutS(0);
            } else {
                pityStat.setSpinsWithoutS(pityStat.getSpinsWithoutS() + 1);
            }
        }
        nxvUserPityStatRepository.save(pityStat);

        // 6. Lưu vào Kho đồ
        nxvUserInventory inventory = new nxvUserInventory();
        inventory.setUser(user);
        inventory.setBoxItem(selectedItem);
        inventory.setStatus("IN_STORAGE");
        nxvUserInventory savedInventory = nxvUserInventoryRepository.save(inventory);

        // 7. Trả về Response
        nxvOpenBoxResponse response = new nxvOpenBoxResponse();
        response.setItemName(selectedItem.getItemName());
        response.setItemImage(selectedItem.getImageUrl());
        response.setRarity(selectedItem.getRarityLevel());
        response.setCurrentBalance(user.getWalletBalance());
        response.setPityTriggered(isPityTriggered);

        // Gán inventoryItem để Frontend có thể dùng ID này bán lại ngay lập tức
        response.setInventoryItem(savedInventory);

        return response;
    }

    @Override
    @Transactional
    public nxvSellItemResponse sellItem(Integer userId, Integer inventoryId) {
        nxvUser user = nxvUserRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        nxvUserInventory inventory = nxvUserInventoryRepository.findByInventoryIdAndUser(inventoryId, user)
                .orElseThrow(() -> new RuntimeException("Vật phẩm không tồn tại hoặc không chính chủ"));

        if (!"IN_STORAGE".equals(inventory.getStatus())) {
            throw new RuntimeException("Vật phẩm này đã bán hoặc đang giao hàng!");
        }

        // Tính giá bán lại (90% giá trị thực)
        BigDecimal sellPrice = inventory.getBoxItem().getMarketValue().multiply(new BigDecimal("0.9"));

        // Cộng tiền
        user.setWalletBalance(user.getWalletBalance().add(sellPrice));
        nxvUserRepository.save(user);

        // Cập nhật trạng thái kho
        inventory.setStatus("SOLD_BACK");
        inventory.setSoldPrice(sellPrice);
        nxvUserInventoryRepository.save(inventory);

        // Ghi log Transaction
        nxvTransaction trans = new nxvTransaction();
        trans.setUser(user);
        trans.setAmount(sellPrice);
        trans.setTransactionType("SELL_BACK");
        trans.setDescription("Bán lại: " + inventory.getBoxItem().getItemName());
        nxvTransactionRepository.save(trans);

        nxvSellItemResponse response = new nxvSellItemResponse();
        response.setItemName(inventory.getBoxItem().getItemName());
        response.setSoldPrice(sellPrice);
        response.setNewBalance(user.getWalletBalance());
        response.setMessage("Bán thành công! Bạn nhận được " + sellPrice + " VNĐ");

        return response;
    }

    private nxvBoxItem performRandomDrop(List<nxvBoxItem> items) {
        double totalWeight = items.stream()
                .mapToDouble(i -> i.getProbability() != null ? i.getProbability() : 0)
                .sum();

        double randomValue = random.nextDouble() * totalWeight;
        double currentSum = 0;

        for (nxvBoxItem item : items) {
            currentSum += item.getProbability() != null ? item.getProbability() : 0;
            if (randomValue <= currentSum) {
                return item;
            }
        }
        // Trường hợp phòng ngừa (tránh null), trả về item cuối cùng (thường là rác D)
        return items.get(items.size() - 1);
    }
}