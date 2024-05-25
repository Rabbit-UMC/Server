package rabbit.umc.com.demo.home.facade;


import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import rabbit.umc.com.demo.community.article.service.ArticleService;
import rabbit.umc.com.demo.community.category.CategoryService;
import rabbit.umc.com.demo.community.domain.Article;
import rabbit.umc.com.demo.community.domain.Category;
import rabbit.umc.com.demo.community.dto.CommunityHomeRes;
import rabbit.umc.com.demo.community.dto.CommunityHomeResV2;
import rabbit.umc.com.demo.home.converter.HomeConverter;
import rabbit.umc.com.demo.mainmission.domain.MainMission;
import rabbit.umc.com.demo.mainmission.service.MainMissionService;

@Component
@Transactional
@RequiredArgsConstructor
public class HomeFacade {

    private final ArticleService articleService;
    private final MainMissionService mainMissionService;
    private final CategoryService categoryService;

    public CommunityHomeRes getHomeV1() {
        List<Article> articleList = articleService.getTopLikeArticle();
        List<MainMission> missionList = mainMissionService.getActiveMainMissionList();
        return HomeConverter.toCommunityHomeRes(missionList, articleList);
    }

    public CommunityHomeResV2 getHomeV2(Long userId) {

        List<MainMission> missionList = mainMissionService.getActiveMainMissionList();
        List<Category> myHostCategories = categoryService.findMyHostCategories(userId);
        List<Article> articleList = articleService.getTopLikeArticle();

        return HomeConverter.toCommunityHomeResV2(missionList, articleList, myHostCategories);
    }
}
