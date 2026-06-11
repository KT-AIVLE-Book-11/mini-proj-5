package ki.aivle.mini_proj5.service;

import ki.aivle.mini_proj5.domain.Comment;
import ki.aivle.mini_proj5.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;

    // 전체 댓글 목록 조회
    @Transactional(readOnly = true)
    public List<Comment> getAll() {
        return commentRepository.findAll();
    }

    // 댓글 등록
    @Transactional
    public Comment create(Comment comment) {
        return commentRepository.save(comment);
    }
}
