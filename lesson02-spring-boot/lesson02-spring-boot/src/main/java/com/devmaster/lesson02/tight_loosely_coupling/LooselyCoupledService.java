package com.devmaster.lesson02.tight_loosely_coupling;

public class LooselyCoupledService {

    // Service chỉ phụ thuộc vào INTERFACE, không phụ thuộc vào lớp cụ thể
    private SortAlgorithm sortAlgorithm;

    public LooselyCoupledService() {}

    // Thuật toán được "tiêm" (inject) vào qua constructor
    public LooselyCoupledService(SortAlgorithm sortAlgorithm) {
        this.sortAlgorithm = sortAlgorithm;
    }

    public void complexBusiness(int[] array) {
        // Service chỉ cần gọi phương thức của interface
        sortAlgorithm.sort(array);
    }

    public static void main(String[] args) {
        // Nơi lắp ráp: Quyết định dùng BubbleSort
        SortAlgorithm bubbleSort = new LooselyBubbleSortAlgorithm();

        // Tiêm BubbleSort vào service
        // (Trong hình đặt tên biến này là sortAlgorithm,
        //  đặt là 'service' sẽ dễ hiểu hơn)
        LooselyCoupledService service = new LooselyCoupledService(bubbleSort);

        int[] arr = {1, 11, 21, 11, 42, 15};

        // Chạy nghiệp vụ
        service.complexBusiness(arr);

        // === Lợi ích ===
        // Nếu bạn có LooselyQuickSortAlgorithm, bạn chỉ cần đổi 2 dòng:
        // SortAlgorithm quickSort = new LooselyQuickSortAlgorithm();
        // LooselyCoupledService service2 = new LooselyCoupledService(quickSort);
        // ... mà không cần sửa code của LooselyCoupledService
    }
}