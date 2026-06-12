package ki.aivle.mini_proj5.controller;

import ki.aivle.mini_proj5.domain.Book;
import ki.aivle.mini_proj5.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    // 1. 전체 도서 목록 조회 + 검색 + 필터링
    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks(
            @RequestParam(required = false) String searchType,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false, defaultValue = "views") String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String direction
    ){
        List<Book> books = bookService.searchBooks(searchType, keyword, genre, sortBy, direction);
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
    public ResponseEntity<Book> createBook(@Valid @RequestBody Book book) {
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

        Book updatedBook = bookService.updateCover(id, coverImageUrl);
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
    public ResponseEntity<Integer> incrementViews(@PathVariable Long id) {
        int updatedViews = bookService.incrementViews(id);
        return ResponseEntity.ok(updatedViews);
    }

    // 8. 좋아요 증가
    @PatchMapping("/{id}/likes")
    public ResponseEntity<Integer> incrementLikes(@PathVariable Long id) {
        int updatedLikes = bookService.incrementLikes(id);
        return ResponseEntity.ok(updatedLikes);
    }
}