package K23CNT1.NguyenXuanVinhLab07.service;

import K23CNT1.NguyenXuanVinhLab07.entity.Category;
import K23CNT1.NguyenXuanVinhLab07.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    // Lấy danh sách
    public List<Category> getAllCategories() {
        System.out.println(categoryRepository.findAll());
        return categoryRepository.findAll();
    }

    // Lấy category theo id
    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    // Cập nhật dữ liệu bằng category: create / update
    public Category saveCategory(Category category) {
        return categoryRepository.save(category);
    }

    // Xóa category
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }
}