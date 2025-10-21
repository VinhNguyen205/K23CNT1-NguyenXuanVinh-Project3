package pkg_default_method;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SortLambdaExample {
    public static void main(String[] args) {
        // Tạo một danh sách (List) các chuỗi
        List<String> list = new ArrayList<>(
                Arrays.asList("Java SpringBoot", "C#", "NetCore", "PHP", "Javascript")
        );

        System.out.println("--- Trước khi sắp xếp ---");
        for (String str : list) {
            System.out.println(str);
        }

        // Sắp xếp, sử dụng biểu thức lambda làm quy tắc so sánh
        Collections.sort(list, (String str1, String str2) -> str1.compareTo(str2));

        // Cách viết ngắn gọn hơn (Java tự suy ra kiểu dữ liệu)
        // Collections.sort(list, (str1, str2) -> str1.compareTo(str2));

        System.out.println("\n--- Sau khi sắp xếp (theo alphabet) ---");
        for (String str : list) {
            System.out.println(str);
        }
    }
}
