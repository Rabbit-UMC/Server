package rabbit.umc.com.demo.community.Comments;

import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rabbit.umc.com.config.BaseException;
import rabbit.umc.com.demo.community.Comments.CommentRepository;
import rabbit.umc.com.demo.community.article.ArticleRepository;
import rabbit.umc.com.demo.community.domain.Article;
import rabbit.umc.com.demo.community.domain.Comment;
import rabbit.umc.com.demo.community.dto.PostCommentReq;
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
            Article article = articleRepository.findArticleById(articleId);
            //존재하는 게시물인지 체크
            if(article.getId() == null){
                throw new EntityNotFoundException("Unable to find Article with id: " + articleId);
            }
            Comment comment = Comment.builder()
                    .article(article)
                    .user(user)
                    .content(postCommentReq.getContent())
                    .build();

            return commentRepository.save(comment).getId();

        }catch (NullPointerException e){
            throw new BaseException(DONT_EXIST_ARTICLE);
        }
    }

    @Transactional
    public Long deleteComment(Long commentsId, Long userId) throws BaseException {
        try {
            Comment findComment = commentRepository.getReferenceById(commentsId);

            //JWT 가 해당 댓글 작성 유저와 동일한지 체크
            if(!findComment.getUser().getId().equals(userId)){
                throw new BaseException(INVALID_USER_JWT);
            }
            //댓글이 존재하는지 체크
            if (findComment.getId() == null) {
                throw new EntityNotFoundException("Unable to find Comment with id: " + commentsId);
            }

            //댓글 삭제
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

            //JWT 가 게시물 작성 유저인지 체크
            if(userId != comment.getArticle().getUser().getId()){
                throw new BaseException(INVALID_USER_JWT);
            }
            //이미 잠긴 댓글인지 체크
            if(comment.getStatus() == INACTIVE){
                throw new BaseException(FAILED_TO_LOCK);
            }

            //댓글 잠금
            comment.lockComment();
            commentRepository.save(comment);

        }catch (EntityNotFoundException e){
            throw new BaseException(DONT_EXIST_COMMENT);
        }
    }
}
