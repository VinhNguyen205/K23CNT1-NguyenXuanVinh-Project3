package K23CNT1.NguyenXuanVinh.Lesson08.controller;

import K23CNT1.NguyenXuanVinh.Lesson08.entity.Author;
import K23CNT1.NguyenXuanVinh.Lesson08.entity.Book;
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
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @Autowired
    private AuthorService authorService;

    private static final String UPLOAD_DIR = "src/main/resources/static/";
    private static final String UPLOAD_PATHFILE = "images/products/";

    // Hiển thị danh sách sách
    @GetMapping
    public String listBooks(Model model) {
        model.addAttribute("books", bookService.getAllBooks());
        return "books/book-list";
    }

    // Hiển thị form thêm mới
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("book", new Book());
        model.addAttribute("authors", authorService.getAllAuthors());
        return "books/book-form";
    }

    // Hiển thị form chỉnh sửa
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Book book = bookService.getBookById(id);
        if (book == null) {
            return "redirect:/books";
        }
        model.addAttribute("book", book);
        model.addAttribute("authors", authorService.getAllAuthors());
        return "books/book-form";
    }

    // Thêm mới sách
    @PostMapping("/new")
    public String saveBook(@ModelAttribute Book book,
                           @RequestParam(value = "authorIds", required = false) List<Long> authorIds,
                           @RequestParam(value = "imageBook", required = false) MultipartFile imageFile) {

        // Xử lý upload ảnh
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                String imgUrl = saveImageFile(imageFile, book.getCode());
                book.setImgUrl(imgUrl);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Error uploading image: " + e.getMessage());
            }
        }

        // Xử lý danh sách tác giả
        if (authorIds != null && !authorIds.isEmpty()) {
            List<Author> authors = new ArrayList<>(authorService.findAllById(authorIds));
            book.setAuthors(authors);
        } else {
            book.setAuthors(new ArrayList<>());
        }

        bookService.saveBook(book);
        return "redirect:/books";
    }

    // Cập nhật sách
    @PostMapping("/update/{id}")
    public String updateBook(@PathVariable Long id,
                             @ModelAttribute Book book,
                             @RequestParam(value = "authorIds", required = false) List<Long> authorIds,
                             @RequestParam(value = "imageBook", required = false) MultipartFile imageFile) {

        // Lấy thông tin sách hiện tại từ DB
        Book existingBook = bookService.getBookById(id);
        if (existingBook == null) {
            return "redirect:/books";
        }

        // Set ID cho book
        book.setId(id);

        // Xử lý upload ảnh mới
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                // Lưu ảnh mới (REPLACE_EXISTING sẽ tự ghi đè file cũ)
                String imgUrl = saveImageFile(imageFile, book.getCode());
                book.setImgUrl(imgUrl);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Error uploading image: " + e.getMessage());
                // Nếu lỗi, giữ nguyên ảnh cũ
                book.setImgUrl(existingBook.getImgUrl());
            }
        } else {
            // Không upload ảnh mới -> giữ nguyên ảnh cũ
            book.setImgUrl(existingBook.getImgUrl());
        }

        // Xử lý danh sách tác giả
        if (authorIds != null && !authorIds.isEmpty()) {
            List<Author> authors = new ArrayList<>(authorService.findAllById(authorIds));
            book.setAuthors(authors);
        } else {
            book.setAuthors(new ArrayList<>());
        }

        bookService.saveBook(book);
        return "redirect:/books";
    }

    // Xóa sách
    @GetMapping("/delete/{id}")
    public String deleteBook(@PathVariable Long id) {
        Book book = bookService.getBookById(id);
        if (book != null) {
            // Xóa ảnh khi xóa sách (tùy chọn)
            deleteOldImage(book.getImgUrl());
            bookService.deleteBook(id);
        }
        return "redirect:/books";
    }

    // ============ PRIVATE HELPER METHODS ============

    /**
     * Lưu file ảnh vào thư mục static
     * @param imageFile File upload từ form
     * @param bookCode Mã sách (dùng để đặt tên file)
     * @return Đường dẫn tương đối của ảnh (VD: /images/products/MS01.png)
     * @throws IOException Nếu có lỗi khi lưu file
     */
    private String saveImageFile(MultipartFile imageFile, String bookCode) throws IOException {
        // Tạo thư mục nếu chưa tồn tại
        Path uploadPath = Paths.get(UPLOAD_DIR + UPLOAD_PATHFILE);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Lấy tên file và extension
        String originalFilename = StringUtils.cleanPath(imageFile.getOriginalFilename());
        String fileExtension = "";
        if (originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        // Tạo tên file mới dựa trên mã sách
        String newFileName = bookCode + fileExtension;
        Path filePath = uploadPath.resolve(newFileName);

        // Thử xóa file cũ trước (nếu tồn tại) để tránh conflict
        try {
            if (Files.exists(filePath)) {
                Thread.sleep(100); // Đợi một chút để OS release file
                Files.deleteIfExists(filePath);
            }
        } catch (Exception e) {
            // Nếu không xóa được, bỏ qua (REPLACE_EXISTING sẽ ghi đè)
            System.out.println("Could not delete old file: " + e.getMessage());
        }

        // Lưu file mới (REPLACE_EXISTING để ghi đè nếu file đã tồn tại)
        try {
            Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            // Nếu vẫn lỗi, thử lại sau 300ms
            try {
                Thread.sleep(300);
                Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                throw new IOException("Failed to save image after retry", e);
            }
        }

        // Trả về đường dẫn tương đối (bắt đầu bằng /)
        return "/" + UPLOAD_PATHFILE + newFileName;
    }

    /**
     * Xóa file ảnh cũ
     * @param imgUrl Đường dẫn ảnh cũ (VD: /images/products/MS01.png)
     */
    private void deleteOldImage(String imgUrl) {
        if (imgUrl != null && !imgUrl.isEmpty()) {
            try {
                // Chuyển đường dẫn URL thành đường dẫn file thực tế
                // VD: /images/products/MS01.png -> src/main/resources/static/images/products/MS01.png
                String filePath = UPLOAD_DIR + imgUrl.substring(1); // Bỏ dấu "/" ở đầu
                Path path = Paths.get(filePath);

                // Đợi một chút để đảm bảo file không bị lock
                Thread.sleep(100);

                // Xóa file nếu tồn tại
                Files.deleteIfExists(path);
                System.out.println("Deleted old image: " + imgUrl);
            } catch (Exception e) {
                // Không làm gì nếu xóa thất bại (file có thể đang được sử dụng)
                System.out.println("Could not delete old image: " + imgUrl + " - " + e.getMessage());
            }
        }
    }
}