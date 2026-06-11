package ki.aivle.mini_proj5.service;

import ki.aivle.mini_proj5.domain.Book;
import ki.aivle.mini_proj5.domain.Comment;
import ki.aivle.mini_proj5.exception.BookNotFoundException;
import ki.aivle.mini_proj5.repository.BookRepository;
import ki.aivle.mini_proj5.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final BookRepository bookRepository;

    // 전체 댓글 목록 조회
    @Transactional(readOnly = true)
    public List<Comment> getAll(Long bookId) {
        if (!bookRepository.existsById(bookId)) {
            throw new BookNotFoundException(bookId);
        }
        return commentRepository.findByBookIdOrderByCreatedAtDesc(bookId);
    }

    // 댓글 등록
    @Transactional
    public Comment create(Long bookId, Comment comment) {
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new BookNotFoundException(bookId));
        comment.setBook(book);
        return commentRepository.save(comment);
    }
}
