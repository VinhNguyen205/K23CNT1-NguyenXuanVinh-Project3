package K23CNT1.NguyenXuanVinh.Lesson08.controller;

import K23CNT1.NguyenXuanVinh.Lesson08.entity.Author;
import K23CNT1.NguyenXuanVinh.Lesson08.entity.Book;
import K23CNT1.NguyenXuanVinh.Lesson08.entity.BookAuthor;
import K23CNT1.NguyenXuanVinh.Lesson08.repository.BookAuthorRepository;
import K23CNT1.NguyenXuanVinh.Lesson08.service.AuthorService;
import K23CNT1.NguyenXuanVinh.Lesson08.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Controller
@RequestMapping("/books")
public class BookController {

    @Autowired
    private BookService bookService;
    @Autowired
    private AuthorService authorService;
    @Autowired
    private BookAuthorRepository bookAuthorRepository; // Thêm cái này

    private static final String UPLOAD_DIR = "src/main/resources/static/";
    private static final String UPLOAD_PATHFILE = "images/products/";

    // ... (Giữ nguyên listBooks, showCreateForm) ...
    @GetMapping
    public String listBooks(Model model) {
        model.addAttribute("books", bookService.getAllBooks());
        return "books/book-list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("book", new Book());
        model.addAttribute("authors", authorService.getAllAuthors());
        return "books/book-form";
    }

    // --- XỬ LÝ LƯU MỚI ---
    @PostMapping("/new")
    public String saveBook(@ModelAttribute Book book,
                           @RequestParam(value = "authorIds", required = false) List<Long> authorIds,
                           @RequestParam(value = "mainAuthorId", required = false) Long mainAuthorId, // ID của chủ biên
                           @RequestParam("imageBook") MultipartFile imageFile) {

        // 1. Xử lý upload ảnh (Giữ nguyên logic cũ)
        if (!imageFile.isEmpty()) {
            try {
                Path uploadPath = Paths.get(UPLOAD_DIR + UPLOAD_PATHFILE);
                if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);
                String originalFilename = StringUtils.cleanPath(imageFile.getOriginalFilename());
                String newFileName = book.getCode() + originalFilename.substring(originalFilename.lastIndexOf("."));
                Path filePath = uploadPath.resolve(newFileName);
                Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                book.setImgUrl("/" + UPLOAD_PATHFILE + newFileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // 2. Lưu sách trước để có ID
        Book savedBook = bookService.saveBook(book);

        // 3. Xử lý lưu danh sách tác giả vào bảng trung gian BookAuthor
        if (authorIds != null && !authorIds.isEmpty()) {
            for (Long authId : authorIds) {
                Author author = authorService.getAuthorById(authId);
                if (author != null) {
                    BookAuthor bookAuthor = new BookAuthor();
                    bookAuthor.setBook(savedBook);
                    bookAuthor.setAuthor(author);

                    // Kiểm tra nếu ID này trùng với ID chủ biên được chọn
                    boolean isMain = (mainAuthorId != null && mainAuthorId.equals(authId));
                    bookAuthor.setIsMainEditor(isMain);

                    bookAuthorRepository.save(bookAuthor);
                }
            }
        }

        return "redirect:/books";
    }

    // ... (Bạn tự cập nhật phần Update tương tự logic saveBook nhé) ...
}