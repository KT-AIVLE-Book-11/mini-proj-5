package ki.aivle.mini_proj5.service;


import ki.aivle.mini_proj5.domain.Book;
import ki.aivle.mini_proj5.exception.BookNotFoundException;
import ki.aivle.mini_proj5.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;

    // 전체 도서 목록 조회
    @Transactional(readOnly = true)
    public List<Book> getAll() {
        return bookRepository.findAll();
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

    // 도서 수정 - 표지
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
            bookRepository.deleteById(id);
        } else {
            throw new BookNotFoundException(id);
        }
    }

    // 조회수 증가
    @Transactional
    public void incrementViews(Long id) {
        Book book = getById(id);
        book.setViews(book.getViews() + 1);
    }

    // 좋아요 증가
    @Transactional
    public void incrementLikes(Long id) {
        Book book = getById(id);
        book.setLikes(book.getLikes() + 1);
    }

}
