package K23CNT1.NguyenXuanVinhLab07.service;

import K23CNT1.NguyenXuanVinhLab07.entity.Category;
import K23CNT1.NguyenXuanVinhLab07.entity.Product;
import K23CNT1.NguyenXuanVinhLab07.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    // Lấy danh sách product
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // Đọc dữ liệu, Bảng Product theo id
    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }

    // Cập nhật: create / update
    public Product saveProduct(Product product) {
        System.out.println(product);
        return productRepository.save(product);
    }

    // Xóa product theo id
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
}