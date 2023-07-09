package rabbit.umc.com.demo.article;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import rabbit.umc.com.config.BaseException;
import rabbit.umc.com.config.BaseResponse;
import rabbit.umc.com.demo.article.dto.PostCommentReq;
import rabbit.umc.com.utils.JwtService;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final JwtService jwtService;

    //댓글 작성 API
    @PostMapping("/app/comments/{articleId}")
    public BaseResponse postComment(@RequestBody PostCommentReq postCommentReq, @PathVariable("articleId") Long articleId) throws BaseException{
        try {
            System.out.println(jwtService.createJwt(1));
            Long userId = (long) jwtService.getUserIdx();
            Long commentId = commentService.postComment(postCommentReq, userId, articleId);
            return new BaseResponse<>("commentId=" + commentId + " 댓글 작성이 완료되었습니다");
        }catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }
}
