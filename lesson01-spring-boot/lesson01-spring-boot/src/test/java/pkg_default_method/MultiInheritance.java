package pkg_default_method;

// Lớp này là public, nên nó phải nằm trong file "MultiInheritance.java"
public class MultiInheritance implements Interface1, Interface2 {

    @Override
    public void method1() {
        Interface1.super.method1();
    }

    // Cung cấp logic mới, ghi đè default method
    public void method2() {
        System.out.println("MultiInheritance.method2");
    }

    // Phương thức main để chạy thử
    public static void main(String[] args) {
        MultiInheritance mi = new MultiInheritance();

        // Sẽ gọi method1()
        mi.method1();

        // Sẽ gọi method2()
        mi.method2();
    }
}
