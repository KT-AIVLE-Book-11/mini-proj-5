package ki.aivle.mini_proj5.controller;

import jakarta.validation.Valid;
import ki.aivle.mini_proj5.domain.Comment;
import ki.aivle.mini_proj5.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    // 댓글 목록 조회
    @GetMapping("/{bookId}/comments")
    public ResponseEntity<List<Comment>> getAllComments(@PathVariable Long bookId) {
        List<Comment> comments = commentService.getAll();
        return ResponseEntity.ok(comments);
    }

    // 댓글 등록
    @PostMapping("/{bookId}/comments")
    public ResponseEntity<Comment> createComment(@PathVariable Long bookId, @Valid @RequestBody Comment comment) {
        Comment createdComment = commentService.create(bookId, comment);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdComment);
    }
}
