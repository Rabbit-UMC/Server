package rabbit.umc.com.demo.community.Comments;

import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import rabbit.umc.com.config.apiPayload.BaseException;
import rabbit.umc.com.config.apiPayload.BaseResponse;
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
    @Tag(name = "make comment")
    @Operation(summary = "댓글 작성 API")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "JWT4001", description = "JWT 토큰을 주세요!",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "JWT4002", description = "JWT 토큰 만료",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "JWT4003", description = "권한 없는 접근",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "ARTICLE4006", description = "존재하지 않는 게시물",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    @Parameters({
            @Parameter(name = "PostCommentReq", description = "댓글 내용"),
            @Parameter(name = "articleId", description = "댓글이 달리는 게시글 id"),
    })
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
    @Tag(name = "delete comment")
    @Operation(summary = "댓글 삭제 API")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "JWT4001", description = "JWT 토큰을 주세요!",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "JWT4002", description = "JWT 토큰 만료",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "JWT4003", description = "권한 없는 접근",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMENT4002", description = "존재하지 않는 댓글",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    @Parameters({
            @Parameter(name = "commentsId", description = "삭제할 댓글 id"),
    })
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
    @Tag(name = "Lock comment")
    @Operation(summary = "댓글 잠금 API")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "JWT4001", description = "JWT 토큰을 주세요!",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "JWT4002", description = "JWT 토큰 만료",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "JWT4003", description = "권한 없는 접근",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMENT4002", description = "존재하지 않는 댓글",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMENT4001", description = "이미 잠긴 댓글",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    @Parameters({
            @Parameter(name = "commentsId", description = "비공개 처리 할 댓글 id"),
    })
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
