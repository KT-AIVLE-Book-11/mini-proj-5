package ki.aivle.mini_proj5.controller;

import jakarta.validation.Valid;
import ki.aivle.mini_proj5.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    // 댓글 목록 조회
    @GetMapping("/{bookId}/comments")
    public ResponseEntity<List<Comment>> getAllComments() {
        List<Comment> comments = commentService.getAll();
        return ResponseEntity.ok(comments);
    }

    // 댓글 등록
    @PostMapping
    public ResponseEntity<Comment> createComment(@Valid @RequestBody Comment comment) {
        Comment createdComment = commentService.create(comment);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdComment);
    }

    // 댓글 수정
    @PatchMapping("/{bookId}/comments/{commentId}")
    public ResponseEntity<Comment> updateCommentInfo(@PathVariable Long id, @RequestBody Comment commentDetails) {
        Comment updatedComment = commentService.updateInfo(id, commentDetails);
        return ResponseEntity.ok(updatedComment);
    }

    @DeleteMapping("/{bookId}/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }
}
