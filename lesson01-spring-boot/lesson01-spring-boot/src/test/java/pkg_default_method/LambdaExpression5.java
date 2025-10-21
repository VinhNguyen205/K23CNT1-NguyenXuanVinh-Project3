package pkg_default_method;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class LambdaExpression5 {
    public static void main(String[] args) {
        List<Book> books = new ArrayList<Book>();
        books.add(new Book(1, "Lập trình Java", 9.95f));
        books.add(new Book(2, "Java SpringBoot", 19.95f));
        books.add(new Book(3, "PHP Laravel", 12.95f));
        books.add(new Book(4, "NetCore API", 29.95f));
        books.add(new Book(5, "Javascript", 13.95f));


        System.out.println("Các sách có giá > 15:");
        books.stream()
                .filter(b -> b.price > 15)
                .forEach(System.out::println);
    }
}
