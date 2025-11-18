package K23CNT1.NguyenXuanVinh.Lesson08.repository;

import K23CNT1.NguyenXuanVinh.Lesson08.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {
}