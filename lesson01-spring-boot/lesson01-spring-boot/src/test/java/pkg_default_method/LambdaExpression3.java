package pkg_default_method;

@FunctionalInterface
interface Calculator1 {
    int add(int a, int b);
}

@FunctionalInterface
interface Calculator2 {
    void add(int a, int b);
}

public class LambdaExpression3 {
    public static void main(String[] args) {

        Calculator1 calc1 = (int a, int b) -> (a + b);
        System.out.println(calc1.add(1, 2)); // In ra 3

        Calculator1 calc2 = (a, b) -> (a + b);
        System.out.println(calc2.add(21, 22)); // In ra 43

        Calculator2 calc3 = (a, b) -> System.out.println(a + b);
        calc3.add(10, 20); // Sẽ in ra 30
    }
}
