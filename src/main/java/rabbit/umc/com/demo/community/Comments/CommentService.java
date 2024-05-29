package rabbit.umc.com.demo.community.Comments;

import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rabbit.umc.com.config.apiPayload.BaseException;
import rabbit.umc.com.demo.community.article.service.ArticleService;
import rabbit.umc.com.demo.community.domain.Article;
import rabbit.umc.com.demo.community.domain.Comment;
import rabbit.umc.com.demo.community.dto.PostCommentReq;
import rabbit.umc.com.demo.converter.CommentConverter;
import rabbit.umc.com.demo.user.Domain.User;
import rabbit.umc.com.demo.user.service.UserQueryService;

import static rabbit.umc.com.config.apiPayload.BaseResponseStatus.*;
import static rabbit.umc.com.demo.base.Status.*;

@ToString
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class CommentService {

    private final ArticleService articleService;
    private final UserQueryService userQueryService;
    private final CommentRepository commentRepository;

    public Comment getComment(Long id) throws BaseException {
        return commentRepository.findById(id)
                .orElseThrow(() -> new BaseException(DONT_EXIST_COMMENT));
    }

    // 로그인 유저가 해당 댓글 작성 유저와 동일한지 체크
    private void checkUserValidity(Comment targetComment, Long userId) throws BaseException {
        if(!targetComment.getUser().getId().equals(userId))
            throw new BaseException(INVALID_USER_JWT);
    }

    // 이미 잠긴 댓글인지 체크
    private void checkCommentLockStatus(Comment targetComment) throws BaseException {
        if(targetComment.getStatus().equals(INACTIVE))
            throw new BaseException(FAILED_TO_LOCK);
    }

    @Transactional
    public Long postComment(PostCommentReq postCommentReq, Long userId, Long articleId) throws BaseException{

        User user = userQueryService.getUserByUserId(userId);
        Article article = articleService.findArticleById(articleId);
        Comment comment = CommentConverter.toComment(article, postCommentReq.getContent(), user);

        return commentRepository.save(comment).getId();
    }

    @Transactional
    public void deleteComment(Comment targetComment, Long userId) throws BaseException {

        checkUserValidity(targetComment, userId);
        commentRepository.delete(targetComment);
    }

    @Transactional
    public void lockComment(Long userId, Long commentsId) throws BaseException {

        Comment targetComment = getComment(commentsId);
        checkUserValidity(targetComment, userId);
        checkCommentLockStatus(targetComment);
        targetComment.lockComment();

        commentRepository.save(targetComment);
    }
}
