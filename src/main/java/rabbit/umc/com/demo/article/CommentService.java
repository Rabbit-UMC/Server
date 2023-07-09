package rabbit.umc.com.demo.article;

import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rabbit.umc.com.demo.article.domain.Article;
import rabbit.umc.com.demo.article.domain.Comment;
import rabbit.umc.com.demo.article.dto.PostCommentReq;
import rabbit.umc.com.demo.user.Domain.User;
import rabbit.umc.com.demo.user.UserRepository;

@ToString
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class CommentService {

    private final ArticleRepository articleRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;


    @Transactional
    public Long postComment(PostCommentReq postCommentReq, Long userId, Long articleId) {
        User user = userRepository.getReferenceById(userId);
        Article article = articleRepository.getReferenceById(articleId);

        Comment comment = new Comment();
        comment.setArticle(article);
        comment.setUser(user);
        comment.setContent(postCommentReq.getContent());
        commentRepository.save(comment);
        return comment.getId();
    }
}
