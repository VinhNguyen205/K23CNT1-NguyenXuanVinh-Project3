package K23CNT1.NguyenXuanVinh.Lesson08.repository;

import K23CNT1.NguyenXuanVinh.Lesson08.entity.BookAuthor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookAuthorRepository extends JpaRepository<BookAuthor, Long> {
    // Custom query nếu cần
}