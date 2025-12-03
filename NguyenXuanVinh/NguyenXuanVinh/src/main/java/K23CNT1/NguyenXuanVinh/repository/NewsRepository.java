package K23CNT1.NguyenXuanVinh.repository;

import K23CNT1.NguyenXuanVinh.entity.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NewsRepository extends JpaRepository<News, Integer> {

    // Lấy tin tức mới nhất lên đầu
    List<News> findAllByOrderByPublishedAtDesc();

    // Tìm tin tức theo tiêu đề (Search bài viết)
    List<News> findByTitleContaining(String keyword);
}