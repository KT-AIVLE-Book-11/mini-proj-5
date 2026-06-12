package ki.aivle.mini_proj5.service;


import ki.aivle.mini_proj5.domain.Book;
import ki.aivle.mini_proj5.exception.BookNotFoundException;
import ki.aivle.mini_proj5.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import ki.aivle.mini_proj5.repository.CommentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Sort;


import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;
    private final CommentRepository commentRepository;

    // 전체 도서 목록 조회 + 검색 + 장르 필터링
    @Transactional(readOnly = true)
    public List<Book> searchBooks(String searchType, String keyword, String genre, String sortBy, String direction) {

        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();
        boolean hasGenre = genre != null && !genre.trim().isEmpty() && !genre.equals("전체");

        if (hasKeyword && hasGenre) {
            if ("author".equals(searchType)) {
                return bookRepository.findByAuthorContainingIgnoreCaseAndGenre(keyword, genre, sort);
            }
            return bookRepository.findByTitleContainingIgnoreCaseAndGenre(keyword, genre, sort);
        }
        if (hasKeyword) {
            if ("author".equals(searchType)) {
                return bookRepository.findByAuthorContainingIgnoreCase(keyword, sort);
            }
            return bookRepository.findByTitleContainingIgnoreCase(keyword, sort);
        }
        if (hasGenre) {
            return bookRepository.findByGenre(genre, sort);
        }
        return bookRepository.findAll(sort);
    }

    // 도서 상세 조회
    @Transactional(readOnly = true)
    public Book getById(Long id) {
        return bookRepository.findById(id).orElseThrow(() -> new BookNotFoundException(id));
    }

    // 도서 등록
    @Transactional
    public Book create(Book book) {
        return bookRepository.save(book);
    }

    // 도서 수정 - 내용
    @Transactional
    public Book updateInfo(Long id, Book bookDetails) {
        Book existing = getById(id);
        if (bookDetails.getTitle() != null) {
            existing.setTitle(bookDetails.getTitle());
        }

        if (bookDetails.getAuthor() != null) {
            existing.setAuthor(bookDetails.getAuthor());
        }

        if (bookDetails.getContent() != null) {
            existing.setContent(bookDetails.getContent());
        }

        if (bookDetails.getGenre() != null) {
            existing.setGenre(bookDetails.getGenre());
        }

        if (bookDetails.getIsPublic() != null) {
            existing.setIsPublic(bookDetails.getIsPublic());
        }
        return bookRepository.save(existing);
    }

    // 도서 수정 - 표지 (프롬프트 사용안하는 변수라 삭제)
    @Transactional
    public Book updateCover(Long id, String coverImageUrl) {
        Book existing = getById(id);

        existing.setCoverImageUrl(coverImageUrl);
        return bookRepository.save(existing);
    }

    // 도서 삭제
    @Transactional
    public void deleteBook(Long id) {
        if (bookRepository.existsById(id)) {
            commentRepository.deleteByBookId(id);
            bookRepository.deleteById(id);
        } else {
            throw new BookNotFoundException(id);
        }
    }

    // 조회수 증가
    @Transactional
    public int incrementViews(Long id) {
        Book book = getById(id);
        book.setViews(book.getViews() + 1);
        return book.getViews();
    }

    // 좋아요 증가
    @Transactional
    public int incrementLikes(Long id) {
        Book book = getById(id);
        book.setLikes(book.getLikes() + 1);
        return book.getLikes();
    }
}
