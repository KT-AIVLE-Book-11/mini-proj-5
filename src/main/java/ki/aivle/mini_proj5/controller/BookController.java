package ki.aivle.mini_proj5.controller;

import ki.aivle.mini_proj5.domain.Book;
import ki.aivle.mini_proj5.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    // 1. 전체 도서 목록 조회
    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks() {
        List<Book> books = bookService.getAll();
        return ResponseEntity.ok(books);
    }

    // 2. 도서 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<Book> getBook(@PathVariable Long id) {
        Book book = bookService.getById(id);
        return ResponseEntity.ok(book);
    }

    // 3. 도서 등록
    @PostMapping
    public ResponseEntity<Book> createBook(@RequestBody Book book) {
        Book createdBook = bookService.create(book);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBook);
    }

    // 4. 도서 수정 (내용)
    @PatchMapping("/{id}")
    public ResponseEntity<Book> updateBookInfo(@PathVariable Long id, @RequestBody Book bookDetails) {
        Book updatedBook = bookService.updateInfo(id, bookDetails);
        return ResponseEntity.ok(updatedBook);
    }

    // 5. 도서 수정 (표지)
    @PatchMapping("/{id}/cover")
    public ResponseEntity<Book> updateBookCover(@PathVariable Long id, @RequestBody Map<String, String> request) {
        String coverImageUrl = request.get("coverImageUrl");
        String prompt = request.get("prompt");

        Book updatedBook = bookService.updateCover(id, coverImageUrl, prompt);
        return ResponseEntity.ok(updatedBook);
    }

    // 6. 도서 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

    // 7. 조회수 증가
    @PatchMapping("/{id}/views")
    public ResponseEntity<Void> incrementViews(@PathVariable Long id) {
        bookService.incrementViews(id);
        return ResponseEntity.ok().build();
    }

    // 8. 좋아요 증가
    @PatchMapping("/{id}/likes")
    public ResponseEntity<Void> incrementLikes(@PathVariable Long id) {
        bookService.incrementLikes(id);
        return ResponseEntity.ok().build();
    }
}