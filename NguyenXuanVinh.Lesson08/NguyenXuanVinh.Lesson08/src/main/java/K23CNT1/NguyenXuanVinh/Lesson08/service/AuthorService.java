package K23CNT1.NguyenXuanVinh.Lesson08.service;

import K23CNT1.NguyenXuanVinh.Lesson08.entity.Author;
import K23CNT1.NguyenXuanVinh.Lesson08.repository.AuthorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthorService {
    @Autowired
    private AuthorRepository authorRepository;

    public List<Author> getAllAuthors() {
        return authorRepository.findAll();
    }

    public Author saveAuthor(Author author) {
        return authorRepository.save(author);
    }

    public Author getAuthorById(Long id) {
        return authorRepository.findById(id).orElse(null);
    }

    public void deleteAuthor(Long id) {
        authorRepository.deleteById(id);
    }

    // Phương thức này dùng để lấy danh sách tác giả từ một danh sách các ID
    public List<Author> findAllById(List<Long> ids) {
        return authorRepository.findAllById(ids);
    }
}