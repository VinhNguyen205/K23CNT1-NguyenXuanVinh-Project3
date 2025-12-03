package K23CNT1.NguyenXuanVinh.repository;

import K23CNT1.NguyenXuanVinh.entity.Category;
import K23CNT1.NguyenXuanVinh.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    // Tìm sản phẩm theo danh mục (VD: Lọc ra tất cả Figure)
    List<Product> findByCategory(Category category);

    // Tìm kiếm sản phẩm theo tên (Chức năng Search)
    // Containing nghĩa là tìm gần đúng (LIKE %keyword%)
    List<Product> findByProductNameContaining(String keyword);

    // Lấy danh sách sản phẩm mới nhất (Sắp xếp ngày tạo giảm dần)
    List<Product> findAllByOrderByCreatedAtDesc();
}