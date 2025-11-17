package K23CNT1.NguyenXuanVinhLab07.repository;

import K23CNT1.NguyenXuanVinhLab07.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
}