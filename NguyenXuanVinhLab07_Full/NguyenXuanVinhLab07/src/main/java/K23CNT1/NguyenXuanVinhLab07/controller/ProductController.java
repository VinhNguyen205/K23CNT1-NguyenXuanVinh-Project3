package K23CNT1.NguyenXuanVinhLab07.controller;

import K23CNT1.NguyenXuanVinhLab07.entity.Product;
import K23CNT1.NguyenXuanVinhLab07.service.CategoryService;
import K23CNT1.NguyenXuanVinhLab07.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/products") // Đổi tên mapping gốc thành /products
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService; // Cần dùng để lấy danh sách category

    // Hiển thị danh sách sản phẩm
    @GetMapping
    public String listProducts(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        return "product/product-list"; // Trả về product/product-list.html
    }

    // Hiển thị form tạo mới sản phẩm
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("product", new Product());
        // Thêm danh sách categories vào model để hiển thị dropdown
        model.addAttribute("categories", categoryService.getAllCategories());
        return "product/product-form"; // Trả về product/product-form.html
    }

    // Hiển thị form chỉnh sửa sản phẩm
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        // Tìm sản phẩm theo ID
        model.addAttribute("product", productService.findById(id).orElse(null));
        // Thêm danh sách categories vào model để hiển thị dropdown
        model.addAttribute("categories", categoryService.getAllCategories());
        return "product/product-form"; // Trả về product/product-form.html
    }

    // Xử lý lưu sản phẩm (cho cả tạo mới)
    // Lưu ý: Tệp HTML của bạn đang trỏ cố định đến /create
    @PostMapping("/create")
    public String saveProduct(@ModelAttribute("product") Product product) {
        productService.saveProduct(product);
        return "redirect:/products";
    }

    // Xử lý cập nhật sản phẩm
    @PostMapping("/update/{id}")
    public String updateProduct(@PathVariable Long id, @ModelAttribute("product") Product product) {
        product.setId(id); // Đảm bảo ID được set cho việc cập nhật
        productService.saveProduct(product);
        return "redirect:/products";
    }

    // Xử lý xóa sản phẩm
    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable("id") Long id) {
        productService.deleteProduct(id);
        return "redirect:/products";
    }
}