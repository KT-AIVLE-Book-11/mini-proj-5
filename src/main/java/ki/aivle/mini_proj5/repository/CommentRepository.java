package ki.aivle.mini_proj5.repository;

import ki.aivle.mini_proj5.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByBookIdOrderByCreatedAtDesc(Long bookId);
}
