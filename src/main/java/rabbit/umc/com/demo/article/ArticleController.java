package rabbit.umc.com.demo.article;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.*;
import rabbit.umc.com.config.BaseException;
import rabbit.umc.com.config.BaseResponse;
import rabbit.umc.com.demo.article.dto.ArticleListRes;
import rabbit.umc.com.demo.article.dto.ArticleRes;
import rabbit.umc.com.demo.article.dto.CommunityHomeRes;
import rabbit.umc.com.demo.article.dto.PostArticleReq;
import rabbit.umc.com.utils.JwtService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;

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
    public BaseResponse deleteArticle(@PathVariable("articleId") Long articleId) throws BaseException{
        articleService.deleteArticle(articleId);
        return new BaseResponse<>(articleId + "번 게시물이 삭제되었습니다");
    }

    /**
     * 게시물 생성 API
     * @param postArticleReq
     * @return
     */
    @PostMapping("/app/article")
    public BaseResponse postArticle(PostArticleReq postArticleReq) throws BaseException{
        Long userId = (long) jwtService.getUserIdx();
        Long articleId = articleService.postArticle(postArticleReq, userId);
        return new BaseResponse<>(articleId);
    }


}