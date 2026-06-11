package ki.aivle.mini_proj5.repository;
import ki.aivle.mini_proj5.domain.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {

    List<Book> findByTitleContaining(String keyword);
    List<Book> findByAuthorContaining(String keyword);
    List<Book> findByGenre(String genre);
    List<Book> findByAuthorContainingAndGenre(String keyword, String genre);
    List<Book> findByTitleContainingAndGenre(String keyword, String genre);
}