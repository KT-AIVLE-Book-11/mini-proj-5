package ki.aivle.mini_proj5.repository;
import ki.aivle.mini_proj5.domain.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {

    List<Book> findByTitleContaining(String keyword, Sort sort);
    List<Book> findByAuthorContaining(String keyword, Sort sort);
    List<Book> findByGenre(String genre, Sort sort);
    List<Book> findByTitle(String title, Sort sort);
    List<Book> findByAuthor(String author, Sort sort);
    List<Book> findByTitleAndAuthor(String title, String author, Sort sort);

    List<Book> findByTitleContainingIgnoreCase(String keyword, Sort sort);
    List<Book> findByAuthorContainingIgnoreCase(String keyword, Sort sort);
    List<Book> findByTitleContainingIgnoreCaseAndGenre(String keyword, String genre, Sort sort);
    List<Book> findByAuthorContainingIgnoreCaseAndGenre(String keyword, String genre, Sort sort);

}