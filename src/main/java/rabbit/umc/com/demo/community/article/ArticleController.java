package rabbit.umc.com.demo.community.article;


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
import org.springframework.web.multipart.MultipartFile;
import rabbit.umc.com.config.apiPayload.BaseException;
import rabbit.umc.com.config.apiPayload.BaseResponse;
import rabbit.umc.com.demo.community.article.service.ArticleService;
import rabbit.umc.com.demo.community.dto.*;
import rabbit.umc.com.utils.JwtService;

import java.io.IOException;
import java.util.List;

@Tag(name = "article", description = "article API")
@RestController
@RequestMapping("/app")
@RequiredArgsConstructor
public class ArticleController {
    private final ArticleService articleService;
    private final JwtService jwtService;
    /**
     * 커뮤니티 홈화면 API
     * @return
     * @throws BaseException
     */
    @Tag(name = "communityHomeV1")
    @Operation(summary = "커뮤니티 홈 화면 조회 API V[1.0]")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공"),
    })
    @GetMapping("/home")
    public BaseResponse<CommunityHomeRes> communityHome () throws BaseException {
        CommunityHomeRes communityHomeRes = articleService.getHomeV1();
        return new BaseResponse<>(communityHomeRes);
    }

    @Tag(name = "communityHomeV2")
    @Operation(summary = "커뮤니티 홈 화면 조회 API V[2.0]")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "JWT4001", description = "JWT 토큰을 주세요!",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "JWT4002", description = "JWT 토큰 만료",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    @GetMapping("/home/v2")
    public BaseResponse<CommunityHomeResV2> communityHomeV2 () throws BaseException {
        try {
            String id = jwtService.createJwt(1);
            System.out.println("토큰" + id);
            Long userId = (long) jwtService.getUserIdx();

            CommunityHomeResV2 communityHomeRes = articleService.getHomeV2(userId);
            return new BaseResponse<>(communityHomeRes);
        }catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 게시판 별 게시물 조회 API
     * @param page 페이징
     * @param categoryId 게시판 카테고리 ID
     * @return
     * @throws BaseException
     */
    @Tag(name = "articleByCategory")
    @Operation(summary = "카테고리 별 게시물 조회 API")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "JWT4001", description = "JWT 토큰을 주세요!",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "JWT4002", description = "JWT 토큰 만료",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    @Parameters({
            @Parameter(name = "page", description = "페이징 넘버 입니다."),
            @Parameter(name = "categoryId", description = "조회할 게시물들의 카테고리 ID 입니다")
    })
    @GetMapping("/article")
    public BaseResponse<ArticleListRes> getArticles(@RequestParam(defaultValue = "0", name = "page") int page, @RequestParam(name = "categoryId") Long categoryId) throws BaseException{
        ArticleListRes articleListRes = articleService.getArticles(page, categoryId);

        return new BaseResponse<>(articleListRes);
    }

    /**
     * 게시물 조회 API
     * @param articleId 게시글 ID
     * @return
     */
    @Tag(name = "articleDetailed")
    @Operation(summary = "게시물 상세 조회 API")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "JWT4001", description = "JWT 토큰을 주세요!",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "JWT4002", description = "JWT 토큰 만료",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "ARTICLE4006", description = "존재하지 않는 게시물",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    @Parameters({
            @Parameter(name = "articleId", description = "조회할 게시물 id 입니다.")
    })
    @GetMapping("/article/{articleId}")
    public BaseResponse<ArticleRes> getArticle(@PathVariable(name = "articleId") Long articleId) throws BaseException{
        try {
            String id = jwtService.createJwt(1);
            System.out.println("토큰" + id);
            Long userId = (long) jwtService.getUserIdx();

            ArticleRes articleRes = articleService.getArticle(articleId, userId);
            return new BaseResponse<>(articleRes);
        }catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }

    }

    /**
     * 게시물 삭제 API
     * @param articleId
     * @return
     */
    @Tag(name = "deleteArticle")
    @Operation(summary = "게시물 삭제 API")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "JWT4001", description = "JWT 토큰을 주세요!",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "JWT4002", description = "JWT 토큰 만료",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "ARTICLE4006", description = "존재하지 않는 게시물",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "JWT4003", description = "게시물 작성자가 아닙니다.",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    @Parameters({
            @Parameter(name = "articleId", description = "조회할 게시물 id 입니다."),
    })
    @DeleteMapping("/article/{articleId}")
    public BaseResponse deleteArticle(@PathVariable("articleId") Long articleId) throws BaseException {
        try{
            System.out.println(jwtService.createJwt(13));
            Long userId = (long) jwtService.getUserIdx();
            articleService.deleteArticle(articleId, userId);
            return new BaseResponse<>(articleId + "번 게시물이 삭제되었습니다");
        }catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }


    /**
     * 게시물 생성 API
     * @param postArticleReq
     * @return
     */
    @Tag(name = "createArticle")
    @Operation(summary = "게시물 생성 API")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "JWT4001", description = "JWT 토큰을 주세요!",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "JWT4002", description = "JWT 토큰 만료",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "415", description = "postArticleReq 전송시 콘텐트 타입 application/json 을명시해주세요",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    @Parameters({
            @Parameter(name = "categoryId", description = "게시글이 저장될 카테고리 id 입니다."),
    })
    @PostMapping(value = "/article" , consumes = {"multipart/form-data"})
    public BaseResponse postArticle(@RequestPart(name = "postArticleReq") PostArticleReq postArticleReq,
                                    @RequestPart(required = false, name = "multipartFiles") List<MultipartFile> multipartFiles ,
                                    @RequestParam("categoryId") Long categoryId) throws BaseException, IOException {
        System.out.println(jwtService.createJwt(1));
        Long userId = (long) jwtService.getUserIdx();
        Long articleId = articleService.postArticle(multipartFiles, postArticleReq, userId, categoryId);
        return new BaseResponse<>(articleId + "번 게시물 생성 완료되었습니다.");
    }

    /**
     * 게시물 수정 API
     * @param patchArticleReq
     * @param articleId 수정하는 게시물 id
     * @return
     * @throws BaseException
     */
    @Tag(name = "patchArticle")
    @Operation(summary = "게시물 수정 API")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "JWT4001", description = "JWT 토큰을 주세요!",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "JWT4002", description = "JWT 토큰 만료",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "ARTICLE4006", description = "게시글 존재 안함",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "JWT4003", description = "게시글 작성자가 아닙니다",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    @Parameters({
            @Parameter(name = "PatchArticleReq", description = "게시글 수정 정보가 포함되어 있습니다."),
            @Parameter(name = "articleId", description = "수정될 게시글 id 입니다."),
    })
    @PatchMapping("/article/{articleId}")
    public BaseResponse patchArticle(@RequestBody PatchArticleReq patchArticleReq, @PathVariable("articleId") Long articleId) throws BaseException {
        try {
            System.out.println(jwtService.createJwt(1));
            Long userId = (long) jwtService.getUserIdx();

            articleService.updateArticle(userId, patchArticleReq, articleId);
            return new BaseResponse<>(articleId + "번 수정완료되었습니다.");
        }catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 게시물 신고 API
     * @param articleId
     * @return
     * @throws BaseException
     */
    @Tag(name = "reportArticle")
    @Operation(summary = "게시물 신고 API")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "JWT4001", description = "JWT 토큰을 주세요!",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "JWT4002", description = "JWT 토큰 만료",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "ARTICLE4006", description = "게시글 존재 안함",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "ARTICLE4001", description = "이미 신고한 게시물",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    @Parameters({
            @Parameter(name = "articleId", description = "신고할 게시글 id 입니다"),
    })
    @PostMapping("/article/{articleId}/report")
    public BaseResponse reportArticle (@PathVariable("articleId") Long articleId) throws BaseException {
        try{
            System.out.println(jwtService.createJwt(1));
            Long userId = (long) jwtService.getUserIdx();
            System.out.println("userId = "+ userId);
            articleService.reportArticle(userId, articleId);
            return new BaseResponse<>(articleId + "번 게시물 신고 완료되었습니다");

        }catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 게시물 좋아요 API
     * @param articleId
     * @return
     * @throws BaseException
     */
    @Tag(name = "likeArticle")
    @Operation(summary = "게시물 좋아요 API")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "JWT4001", description = "JWT 토큰을 주세요!",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "JWT4002", description = "JWT 토큰 만료",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "ARTICLE4006", description = "게시글 존재 안함",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "ARTICLE4002", description = "이미 좋아한 게시물",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    @Parameters({
            @Parameter(name = "articleId", description = "좋아요할 게시글 id 입니다"),
    })
    @PostMapping("/article/{articleId}/like")
    public BaseResponse likeArticle(@PathVariable("articleId") Long articleId) throws BaseException{
        try{

            Long userId = (long) jwtService.getUserIdx();
            articleService.likeArticle(userId, articleId);
            return new BaseResponse<>("좋아요 완료되었습니다.");
        }catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 게시물 좋아요 취소 API
     * @param articleId
     * @return
     * @throws BaseException
     */
    @Tag(name = "unlikeArticle")
    @Operation(summary = "게시물 좋아요 취소 API")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "JWT4001", description = "JWT 토큰을 주세요!",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "JWT4002", description = "JWT 토큰 만료",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "ARTICLE4006", description = "게시글 존재 안함",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "ARTICLE4003", description = "좋아요 하지 않는 게시물",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    @Parameters({
            @Parameter(name = "articleId", description = "좋아요 취소 게시글 id 입니다"),
    })
    @DeleteMapping("/article/{articleId}/unlike")
    public BaseResponse unLikeArticle(@PathVariable("articleId")Long articleId) throws BaseException{
        try {
            Long userId = (long) jwtService.getUserIdx();
            articleService.unLikeArticle(userId, articleId);
            return new BaseResponse<>(articleId + "번 게시물 좋아요 취소되었습니다");
        }catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 인기 게시물 조회 API
     * @param page
     * @return
     * @throws BaseException
     */
    @Tag(name = "popularArticle")
    @Operation(summary = "인기 게시물 조회 API" )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공"),
    })
    @Parameters({
            @Parameter(name = "page", description = "페이징 번호 입니다"),
    })
    @GetMapping("/popular-posts")
    public BaseResponse<List<GetPopularArticleRes>> getPopularArticles(@RequestParam(defaultValue = "0", name = "page") int page) throws BaseException {

        try {
            List<GetPopularArticleRes> popularArticles = articleService.popularArticle(page);
            return new BaseResponse<>(popularArticles);
        }catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }
}