package ki.aivle.mini_proj5.service;

import ki.aivle.mini_proj5.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;

    // 전체 댓글 목록 조회
    @Transactional(readOnly = true)
    public List<Comment> getAll() {
        return commentRepository.findAll();
    }

    // 댓글 상세 조회
    @Transactional(readOnly = true)
    public Comment getById(Long id) {
        return commentRepository.findById(id).orElseThrow(() -> new CommentNotFoundException(id));
    }

    // 댓글 등록
    @Transactional
    public Comment create(Comment comment) {
        return commentRepository.save(comment);
    }

    // 댓글 수정
    @Transactional
    public Comment updateInfo(Lond id, Comment commentDetails) {
        Comment existing = getById(id);

        if (commentDetails.getContent() != null) {
            existing.setContent(commentDetails.getContent());
        }

        if (commentDetails.getRating() != null) {
            existing.setRating(commentDetails.getRating());
        }
        return commentRepository.save(existing);
    }

    // 댓글 삭제
    @Transactional
    public void deleteComment(Long id) {
        if (commentRepository.existsById(id)) {
            commentRepository.deleteById(id);
        } else {
            throw new CommentNotFoundException(id);
        }
    }
}
