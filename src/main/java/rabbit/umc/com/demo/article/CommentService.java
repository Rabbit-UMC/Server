package rabbit.umc.com.demo.article;

import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rabbit.umc.com.config.BaseException;
import rabbit.umc.com.config.BaseResponse;
import rabbit.umc.com.config.BaseResponseStatus;
import rabbit.umc.com.demo.Status;
import rabbit.umc.com.demo.article.domain.Article;
import rabbit.umc.com.demo.article.domain.Comment;
import rabbit.umc.com.demo.article.dto.PostCommentReq;
import rabbit.umc.com.demo.user.Domain.User;
import rabbit.umc.com.demo.user.UserRepository;

import javax.persistence.EntityNotFoundException;

import static rabbit.umc.com.config.BaseResponseStatus.*;
import static rabbit.umc.com.demo.Status.*;

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
    public Long postComment(PostCommentReq postCommentReq, Long userId, Long articleId) throws BaseException{
        try {
            User user = userRepository.getReferenceById(userId);
            Article article = articleRepository.getReferenceById(articleId);
            if(article.getContent() == null){
                throw new EntityNotFoundException("Unable to find Article with id: " + articleId);
            }
            Comment comment = new Comment();
            comment.setArticle(article);
            comment.setUser(user);
            comment.setContent(postCommentReq.getContent());
            commentRepository.save(comment);
            return comment.getId();
        }catch (EntityNotFoundException e){
            throw new BaseException(DONT_EXIST_ARTICLE);
        }

    }

    @Transactional
    public Long deleteComment(Long commentsId, Long userId) throws BaseException {
        try {
            Comment findComment = commentRepository.getReferenceById(commentsId);
            if(!findComment.getUser().getId().equals(userId)){
                throw new BaseException(INVALID_USER_JWT);
            }
            if (findComment.getContent() == null) {
                throw new EntityNotFoundException("Unable to find Comment with id: " + commentsId);
            }
            commentRepository.delete(findComment);
            return commentsId;
        }catch (EntityNotFoundException e){
            throw new BaseException(DONT_EXIST_COMMENT);
        }
    }

    @Transactional
    public void lockComment(Long userId, Long commentsId) throws BaseException {
        try {
            Comment comment = commentRepository.getReferenceById(commentsId);
            if(userId != comment.getArticle().getUser().getId()){
                throw new BaseException(INVALID_USER_JWT);
            }
            if(comment.getStatus() == INACTIVE){
                throw new BaseException(FAILED_TO_LOCK);
            }
            comment.setStatus(INACTIVE);
            commentRepository.save(comment);

        }catch (EntityNotFoundException e){
            throw new BaseException(DONT_EXIST_COMMENT);
        }

    }
}
