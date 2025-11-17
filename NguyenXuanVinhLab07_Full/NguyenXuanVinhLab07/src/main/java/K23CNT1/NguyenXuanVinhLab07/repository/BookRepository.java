package K23CNT1.NguyenXuanVinhLab07.repository;

import K23CNT1.NguyenXuanVinhLab07.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
}