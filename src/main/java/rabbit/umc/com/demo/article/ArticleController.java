package rabbit.umc.com.demo.article;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import rabbit.umc.com.config.BaseException;
import rabbit.umc.com.config.BaseResponse;
import rabbit.umc.com.demo.article.dto.CommunityHomeRes;
import rabbit.umc.com.utils.JwtService;

@Controller
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;

    @GetMapping("/app/home")
    public BaseResponse<CommunityHomeRes> communityHome () throws BaseException {
        CommunityHomeRes communityHomeRes = articleService.getHome();
        return new BaseResponse<>(communityHomeRes);
    }


}
