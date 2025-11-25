package K23CNT1.NguyenXuanVinh.Lesson08.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "book_author")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookAuthor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private Author author;

    private Boolean isMainEditor; // true = Chủ biên, false = Đồng tác giả
}