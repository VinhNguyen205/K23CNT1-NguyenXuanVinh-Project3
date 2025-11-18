package K23CNT1.NguyenXuanVinh.Lesson08.controller;

import K23CNT1.NguyenXuanVinh.Lesson08.entity.Configuration;
import K23CNT1.NguyenXuanVinh.Lesson08.entity.Product;
import K23CNT1.NguyenXuanVinh.Lesson08.service.ConfigurationService;
import K23CNT1.NguyenXuanVinh.Lesson08.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;
    @Autowired
    private ConfigurationService configurationService; // Cần để lấy danh sách config

    @GetMapping
    public String listProducts(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        return "products/product-list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("product", new Product());
        // Lấy danh sách config để hiển thị checkbox
        model.addAttribute("allConfigs", configurationService.getAllConfigurations());
        return "products/product-form";
    }

    @PostMapping("/new")
    public String saveProduct(@ModelAttribute Product product,
                              @RequestParam(value = "configIds", required = false) List<Long> configIds) {

        if (configIds != null && !configIds.isEmpty()) {
            List<Configuration> configs = configurationService.findAllById(configIds);
            product.setConfigurations(configs);
        } else {
            product.setConfigurations(new ArrayList<>());
        }
        productService.saveProduct(product);
        return "redirect:/products";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("product", productService.getProductById(id));
        // Lấy danh sách config để hiển thị checkbox
        model.addAttribute("allConfigs", configurationService.getAllConfigurations());
        return "products/product-form";
    }

    @PostMapping("/update/{id}")
    public String updateProduct(@PathVariable Long id,
                                @ModelAttribute Product product,
                                @RequestParam(value = "configIds", required = false) List<Long> configIds) {
        product.setId(id);
        if (configIds != null && !configIds.isEmpty()) {
            List<Configuration> configs = configurationService.findAllById(configIds);
            product.setConfigurations(configs);
        } else {
            product.setConfigurations(new ArrayList<>());
        }
        productService.saveProduct(product);
        return "redirect:/products";
    }

    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return "redirect:/products";
    }
}