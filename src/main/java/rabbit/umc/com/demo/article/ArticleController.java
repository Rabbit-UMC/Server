package rabbit.umc.com.demo.article;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import rabbit.umc.com.config.BaseException;
import rabbit.umc.com.config.BaseResponse;
import rabbit.umc.com.demo.article.dto.ArticleListRes;
import rabbit.umc.com.demo.article.dto.CommunityHomeRes;
import rabbit.umc.com.utils.JwtService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;

    /**
     * 커뮤니티 홈화면
     * @return
     * @throws BaseException
     */
    @GetMapping("/app/home")
    public BaseResponse<CommunityHomeRes> communityHome () throws BaseException {
        CommunityHomeRes communityHomeRes = articleService.getHome();
        return new BaseResponse<>(communityHomeRes);
    }

    @GetMapping("/app/article")
    public BaseResponse<List<ArticleListRes>> getArticles(@RequestParam(defaultValue = "0", name = "page") int page, @RequestParam(name = "categoryId") Long categoryId) throws BaseException{
        List<ArticleListRes> articleListRes = articleService.getArticles(page, categoryId);

        return new BaseResponse<>(articleListRes);
    }



}
