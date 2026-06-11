package ki.aivle.mini_proj5.repository;
import ki.aivle.mini_proj5.domain.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {

    List<Book> findByTitleContaining(String keyword);
    List<Book> findByAuthorContaining(String keyword);
    List<Book> findByGenre(String genre);
    List<Book> findByTitle(String title);
    List<Book> findByAuthor(String author);
    List<Book> findByTitleAndAuthor(String title, String author);

}