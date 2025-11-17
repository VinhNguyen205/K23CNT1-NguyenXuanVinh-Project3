package K23CNT1.NguyenXuanVinhLab07.controller;

import K23CNT1.NguyenXuanVinhLab07.entity.Book;
import K23CNT1.NguyenXuanVinhLab07.service.BookService;
import K23CNT1.NguyenXuanVinhLab07.service.CategoryService; // Cần import CategoryService
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/books") // Đặt mapping gốc là /books
public class BookController {

    @Autowired
    private BookService bookService;

    @Autowired
    private CategoryService categoryService; // Tiêm CategoryService để lấy danh sách

    // Hiển thị danh sách Book
    @GetMapping
    public String listBooks(Model model) {
        model.addAttribute("books", bookService.getAllBooks());
        return "book/book-list"; // Trỏ đến templates/book/book-list.html
    }

    // Hiển thị form tạo mới Book
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("book", new Book());
        // Lấy tất cả categories và đưa vào model cho dropdown
        model.addAttribute("categories", categoryService.getAllCategories());
        return "book/book-form"; // Trỏ đến templates/book/book-form.html
    }

    // Hiển thị form chỉnh sửa Book
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        // Tìm Book theo ID
        model.addAttribute("book", bookService.findById(id).orElse(null));
        // Lấy tất cả categories và đưa vào model cho dropdown
        model.addAttribute("categories", categoryService.getAllCategories());
        return "book/book-form"; // Trỏ đến templates/book/book-form.html
    }

    // Xử lý lưu Book (Tạo mới)
    @PostMapping("/create")
    public String saveBook(@ModelAttribute("book") Book book) {
        bookService.saveBook(book);
        return "redirect:/books";
    }

    // Xử lý lưu Book (Cập nhật)
    @PostMapping("/update/{id}")
    public String updateBook(@PathVariable Long id, @ModelAttribute("book") Book book) {
        book.setId(id); // Đảm bảo set ID cho đối tượng Book
        bookService.saveBook(book);
        return "redirect:/books";
    }

    // Xử lý xóa Book
    @GetMapping("/delete/{id}")
    public String deleteBook(@PathVariable("id") Long id) {
        bookService.deleteBook(id);
        return "redirect:/books";
    }
}