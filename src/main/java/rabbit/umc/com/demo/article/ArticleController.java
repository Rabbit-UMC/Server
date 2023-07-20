package rabbit.umc.com.demo.article;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import rabbit.umc.com.config.BaseException;
import rabbit.umc.com.config.BaseResponse;
import rabbit.umc.com.config.BaseTimeEntity;
import rabbit.umc.com.demo.article.dto.*;
import rabbit.umc.com.utils.JwtService;
import rabbit.umc.com.utils.S3Uploader;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;
    private final S3Uploader s3Uploader;
    private final JwtService jwtService;
    /**
     * 커뮤니티 홈화면 API
     * @return
     * @throws BaseException
     */
    @GetMapping("/app/home")
    public BaseResponse<CommunityHomeRes> communityHome () throws BaseException {
        CommunityHomeRes communityHomeRes = articleService.getHome();
        return new BaseResponse<>(communityHomeRes);
    }

    /**
     * 게시판 별 게시물 조회 API
     * @param page 페이징
     * @param categoryId 게시판 카테고리 ID
     * @return
     * @throws BaseException
     */
    @GetMapping("/app/article")
    public BaseResponse<List<ArticleListRes>> getArticles(@RequestParam(defaultValue = "0", name = "page") int page, @RequestParam(name = "categoryId") Long categoryId) throws BaseException{
        List<ArticleListRes> articleListRes = articleService.getArticles(page, categoryId);

        return new BaseResponse<>(articleListRes);
    }

    /**
     * 게시글 조회 API
     * @param articleId 게시글 ID
     * @return
     */
    @GetMapping("/app/article/{articleId}")
    public BaseResponse<ArticleRes> getArticle(@PathVariable(name = "articleId") Long articleId) throws BaseException{
        ArticleRes articleRes = articleService.getArticle(articleId);
        return new BaseResponse<>(articleRes);
    }

    /**
     * 게시물 삭제 API
     * @param articleId
     * @return
     */
    @DeleteMapping("/app/article/{articleId}")
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
     * 이미지 저장 API
     * @param multipartFiles
     * @return
     * @throws IOException
     */
    @PostMapping("/file")
    public BaseResponse<List<String>> uploadFile(@RequestPart(value = "file") List<MultipartFile> multipartFiles, @RequestParam(name = "path") String path) throws IOException {
        List<String> filePathList = new ArrayList<>();
        for (MultipartFile multipartFile : multipartFiles) {
            String filePath = s3Uploader.upload(multipartFile, path );
            filePathList.add(filePath);
        }
        return new BaseResponse<>(filePathList);
    }

    /**
     * 게시물 생성 API
     * @param postArticleReq
     * @return
     */
    @PostMapping("/app/article")
    public BaseResponse postArticle( @RequestBody PostArticleReq postArticleReq, @RequestParam("categoryId") Long categoryId) throws BaseException, IOException {
        System.out.println(jwtService.createJwt(1));
        Long userId = (long) jwtService.getUserIdx();
        Long articleId = articleService.postArticle(postArticleReq, userId, categoryId);
        return new BaseResponse<>(articleId);
    }

    /**
     * 게시글 수정 API
     * @param patchArticleReq
     * @param articleId 수정하는 게시물 id
     * @return
     * @throws BaseException
     */
    @PatchMapping("/app/article/{articleId}")
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
    @PostMapping("/app/article/{articleId}/report")
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
    @PostMapping("/app/article/{articleId}/like")
    public BaseResponse likeArticle(@PathVariable("articleId") Long articleId) throws BaseException{
        try{
            System.out.println(jwtService.createJwt(1));
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
    @DeleteMapping("/app/article/{articleId}/unlike")
    public BaseResponse unLikeArticle(@PathVariable("articleId")Long articleId) throws BaseException{
        try {
            System.out.println(jwtService.createJwt(1));
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
    @GetMapping("/app/popular-posts")
    public BaseResponse<List<GetPopularArticleRes>> getPopularArticles(@RequestParam(defaultValue = "0", name = "page") int page) throws BaseException {

        try {
            List<GetPopularArticleRes> popularArticles = articleService.popularArticle(page);
            return new BaseResponse<>(popularArticles);
        }catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }


    }







}