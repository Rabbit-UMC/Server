package rabbit.umc.com.demo.home.converter;

import java.util.List;
import java.util.stream.Collectors;
import rabbit.umc.com.demo.community.domain.Article;
import rabbit.umc.com.demo.community.domain.Category;
import rabbit.umc.com.demo.home.dto.CommunityHomeRes;
import rabbit.umc.com.demo.home.dto.CommunityHomeRes.MainMissionDto;
import rabbit.umc.com.demo.home.dto.CommunityHomeRes.PopularArticleDto;
import rabbit.umc.com.demo.home.dto.CommunityHomeResV2;
import rabbit.umc.com.demo.home.dto.CommunityHomeResV2.MainMissionDtoV2;
import rabbit.umc.com.demo.home.dto.CommunityHomeResV2.PopularArticleDtoV2;
import rabbit.umc.com.demo.converter.ArticleConverter;
import rabbit.umc.com.demo.converter.MainMissionConverter;
import rabbit.umc.com.demo.mainmission.domain.MainMission;

public class HomeConverter {

    public static CommunityHomeRes toCommunityHomeRes(List<MainMission> missionList, List<Article> articleList){
        List<PopularArticleDto> popularArticleList = ArticleConverter.toPopularArticleDto(articleList);
        List<MainMissionDto> mainMissionDtoList = MainMissionConverter.toMainMissionDtoList(missionList);

        return CommunityHomeRes.builder()
                .mainMission(mainMissionDtoList)
                .popularArticle(popularArticleList)
                .build();
    }

    public static CommunityHomeResV2 toCommunityHomeResV2(List<MainMission> missionList, List<Article> articleList, List<Category> hostCategories){
        List<PopularArticleDtoV2> popularArticleList = ArticleConverter.toPopularArticleDtoV2(articleList);
        List<MainMissionDtoV2> mainMissionDtoList = MainMissionConverter.toMainMissionDtoV2(missionList);

        return CommunityHomeResV2.builder()
                .mainMission(mainMissionDtoList)
                .popularArticle(popularArticleList)
                .userHostCategory(hostCategories.stream().map(Category::getId).collect(Collectors.toList()))
                .build();
    }
}
