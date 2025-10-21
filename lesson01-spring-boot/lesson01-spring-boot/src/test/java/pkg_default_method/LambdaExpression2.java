package pkg_default_method;

@FunctionalInterface
interface SayHello2 {
    void sayHello(String name);
}

public class LambdaExpression2 {
    public static void main(String[] args) {


        SayHello2 sayHello = name -> {
            System.out.println("Hello " + name);
        };

        sayHello.sayHello("Vinh");
        sayHello.sayHello("Nguyen");
    }
}
