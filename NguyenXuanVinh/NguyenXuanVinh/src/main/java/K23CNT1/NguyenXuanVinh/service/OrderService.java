package K23CNT1.NguyenXuanVinh.service;

import K23CNT1.NguyenXuanVinh.entity.Order;

public interface OrderService {
    Order placeOrder(Integer userId, String receiverName, String phoneNumber, String address);
}