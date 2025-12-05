package K23CNT1.NguyenXuanVinh.nxvservice.impl;

import K23CNT1.NguyenXuanVinh.nxvdto.nxvOpenBoxResponse;
import K23CNT1.NguyenXuanVinh.nxvdto.nxvSellItemResponse;
import K23CNT1.NguyenXuanVinh.nxventity.*;
import K23CNT1.NguyenXuanVinh.nxvrepository.*;
import K23CNT1.NguyenXuanVinh.nxvservice.nxvBlindBoxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

@Service
public class nxvBlindBoxServiceImpl implements nxvBlindBoxService {

    @Autowired private nxvUserRepository nxvUserRepository;
    @Autowired private nxvBlindBoxRepository nxvBlindBoxRepository;
    @Autowired private nxvBoxItemRepository nxvBoxItemRepository;
    @Autowired private nxvUserInventoryRepository nxvUserInventoryRepository;
    @Autowired private nxvTransactionRepository nxvTransactionRepository;
    @Autowired private nxvUserPityStatRepository nxvUserPityStatRepository;

    private final Random random = new Random();

    @Override
    @Transactional
    public nxvOpenBoxResponse buyAndOpenBox(Integer userId, Integer boxId) {
        nxvUser user = nxvUserRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        nxvBlindBox box = nxvBlindBoxRepository.findById(boxId).orElseThrow(() -> new RuntimeException("Box not found"));

        if (user.getWalletBalance().compareTo(box.getPrice()) < 0) throw new RuntimeException("Không đủ tiền!");

        user.setWalletBalance(user.getWalletBalance().subtract(box.getPrice()));
        nxvUserRepository.save(user);

        nxvTransaction trans = new nxvTransaction();
        trans.setUser(user);
        trans.setAmount(box.getPrice().negate());
        trans.setTransactionType("BUY_BOX");
        trans.setDescription("Mua hộp: " + box.getBoxName());
        nxvTransactionRepository.save(trans);

        List<nxvBoxItem> items = nxvBoxItemRepository.findByBlindBox(box);

        // Pity Logic
        nxvUserPityStat pityStat = nxvUserPityStatRepository.findByUserAndBlindBox(user, box)
                .orElseGet(() -> {
                    nxvUserPityStat newStat = new nxvUserPityStat();
                    newStat.setUser(user);
                    newStat.setBlindBox(box);
                    newStat.setSpinsWithoutS(0);
                    return nxvUserPityStatRepository.save(newStat);
                });

        nxvBoxItem selectedItem;
        boolean isPity = false;

        if (pityStat.getSpinsWithoutS() >= 50) {
            selectedItem = items.stream().filter(i -> "S".equals(i.getRarityLevel())).findFirst().orElse(items.get(0));
            isPity = true;
            pityStat.setSpinsWithoutS(0);
        } else {
            selectedItem = performRandomDrop(items);
            if ("S".equals(selectedItem.getRarityLevel())) pityStat.setSpinsWithoutS(0);
            else pityStat.setSpinsWithoutS(pityStat.getSpinsWithoutS() + 1);
        }
        nxvUserPityStatRepository.save(pityStat);

        nxvUserInventory inventory = new nxvUserInventory();
        inventory.setUser(user);
        inventory.setBoxItem(selectedItem);
        inventory.setStatus("IN_STORAGE");
        nxvUserInventoryRepository.save(inventory);

        nxvOpenBoxResponse response = new nxvOpenBoxResponse();
        response.setItemName(selectedItem.getItemName());
        response.setItemImage(selectedItem.getImageUrl());
        response.setRarity(selectedItem.getRarityLevel());
        response.setCurrentBalance(user.getWalletBalance());
        response.setPityTriggered(isPity);
        response.setInventoryItem(inventory);

        return response;
    }

    @Override
    @Transactional
    public nxvSellItemResponse sellItem(Integer userId, Integer inventoryId) {
        nxvUser user = nxvUserRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        nxvUserInventory inventory = nxvUserInventoryRepository.findByInventoryIdAndUser(inventoryId, user)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        if (!"IN_STORAGE".equals(inventory.getStatus())) throw new RuntimeException("Item unavailable");

        BigDecimal sellPrice = inventory.getBoxItem().getMarketValue().multiply(new BigDecimal("0.9"));
        user.setWalletBalance(user.getWalletBalance().add(sellPrice));
        nxvUserRepository.save(user);

        inventory.setStatus("SOLD_BACK");
        inventory.setSoldPrice(sellPrice);
        nxvUserInventoryRepository.save(inventory);

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
        response.setMessage("Bán thành công! +" + sellPrice);
        return response;
    }

    private nxvBoxItem performRandomDrop(List<nxvBoxItem> items) {
        double totalWeight = items.stream().mapToDouble(i -> i.getProbability() != null ? i.getProbability() : 0).sum();
        double randomValue = random.nextDouble() * totalWeight;
        double currentSum = 0;
        for (nxvBoxItem item : items) {
            currentSum += item.getProbability() != null ? item.getProbability() : 0;
            if (randomValue <= currentSum) return item;
        }
        return items.get(items.size() - 1);
    }
}