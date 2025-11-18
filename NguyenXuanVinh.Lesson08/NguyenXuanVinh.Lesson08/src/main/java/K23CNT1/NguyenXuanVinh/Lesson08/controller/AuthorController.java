package K23CNT1.NguyenXuanVinh.Lesson08.controller;

import K23CNT1.NguyenXuanVinh.Lesson08.entity.Author;
import K23CNT1.NguyenXuanVinh.Lesson08.service.AuthorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/authors") // Dòng này đăng ký đường dẫn /authors
public class AuthorController {

    @Autowired
    private AuthorService authorService;

    // Hiển thị danh sách tác giả
    @GetMapping
    public String listAuthors(Model model) {
        model.addAttribute("authors", authorService.getAllAuthors());
        return "authors/author-list"; // Trỏ đến templates/authors/author-list.html
    }

    // Hiển thị form tạo mới
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("author", new Author());
        return "authors/author-form"; // Trỏ đến templates/authors/author-form.html
    }

    // Xử lý tạo mới
    @PostMapping("/new")
    public String saveAuthor(@ModelAttribute("author") Author author) {
        authorService.saveAuthor(author);
        return "redirect:/authors";
    }

    // Hiển thị form sửa
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        model.addAttribute("author", authorService.getAuthorById(id));
        return "authors/author-form";
    }

    // Xử lý cập nhật
    @PostMapping("/update/{id}")
    public String updateAuthor(@PathVariable Long id, @ModelAttribute("author") Author author) {
        author.setId(id);
        authorService.saveAuthor(author);
        return "redirect:/authors";
    }

    // Xử lý xóa
    @GetMapping("/delete/{id}")
    public String deleteAuthor(@PathVariable("id") Long id) {
        authorService.deleteAuthor(id);
        return "redirect:/authors";
    }
}