package rabbit.umc.com.demo.community.Comments;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import rabbit.umc.com.config.BaseException;
import rabbit.umc.com.config.BaseResponse;
import rabbit.umc.com.demo.community.dto.PostCommentReq;
import rabbit.umc.com.utils.JwtService;

@Api(tags = {"댓글 관련 Controller"})
@RestController
@RequiredArgsConstructor
@RequestMapping("/app/comments")
public class CommentController {

    private final CommentService commentService;
    private final JwtService jwtService;

    /**
     * 댓글 작성 API
     * @param postCommentReq
     * @param articleId
     * @return
     * @throws BaseException
     */
    @ApiOperation(value = "댓글 작성 하는 메소드")
    @PostMapping("/{articleId}")
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

    /**
     * 댓글 삭제 API
     * @param commentsId
     * @return
     * @throws BaseException
     */
    @ApiOperation(value = "댓글 삭제 하는 메소드")
    @DeleteMapping("/{commentsId}")
    public BaseResponse deleteComment(@PathVariable("commentsId") Long commentsId)throws BaseException{
        try {
            System.out.println(jwtService.createJwt(1));
            Long userId = (long) jwtService.getUserIdx();
            Long deleteId = commentService.deleteComment(commentsId, userId);

            return new BaseResponse<>(deleteId + "번 댓글이 삭제되었습니다.");
        }catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 댓글 잠금 API
     * @param commentsId
     * @return
     */
    @ApiOperation(value = "댓글 잠금 기능 메소드")
    @PatchMapping("/{commentsId}/lock")
    public BaseResponse lockComment(@PathVariable("commentsId") Long commentsId) {
        try {
            System.out.println(jwtService.createJwt(1));
            Long userId = (long) jwtService.getUserIdx();
            commentService.lockComment(userId, commentsId);
            return new BaseResponse(commentsId + "번 댓글이 잠겼습니다.");
        }catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }







}
