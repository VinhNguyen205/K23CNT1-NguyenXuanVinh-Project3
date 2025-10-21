package com.devmaster.lesson02.tight_loosely_coupling;

import java.util.Arrays;

public class TightCouplingService {
    // === Vấn đề "Tight Coupling" (Liên kết ràng buộc) nằm ở đây ===
    // Service này tạo ra một đối tượng BubbleSortAlgorithm CỨNG
    private BubbleSortAlgorithm bubbleSortAlgorithm = new BubbleSortAlgorithm();
    // === ===

    public TightCouplingService() {}

    public TightCouplingService(BubbleSortAlgorithm bubbleSortAlgorithm) {
        this.bubbleSortAlgorithm = bubbleSortAlgorithm;
    }

    public void complexBusinessSort(int[] arr) {
        // Service này SỬ DỤNG thuật toán đã tạo
        bubbleSortAlgorithm.sort(arr);
        // In kết quả
        Arrays.stream(arr).forEach(System.out::println);
    }

    public static void main(String[] args) {
        // Tạo service
        TightCouplingService tCouplingService = new TightCouplingService();

        int[] arr = {11, 21, 11, 42, 15};

        // Gọi nghiệp vụ
        tCouplingService.complexBusinessSort(arr);
    }
}