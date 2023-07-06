package rabbit.umc.com.demo.article;

import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rabbit.umc.com.demo.Status;
import rabbit.umc.com.demo.article.domain.Article;
import rabbit.umc.com.demo.article.dto.CommunityHomeRes;
import rabbit.umc.com.demo.article.dto.PopularArticleDto;
import rabbit.umc.com.demo.mainmission.MainMissionRepository;
import rabbit.umc.com.demo.mainmission.domain.MainMission;
import rabbit.umc.com.demo.mainmission.dto.MainMissionListDto;

import java.awt.print.Pageable;
import java.util.List;
import java.util.stream.Collectors;

import static rabbit.umc.com.demo.Status.ACTIVE;

@ToString
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final MainMissionRepository mainMissionRepository;


    public CommunityHomeRes getHome() {
        CommunityHomeRes communityHomeRes = new CommunityHomeRes();

        PageRequest pageable = PageRequest.of(0,4);
        List<Article> articleList = articleRepository.findPopularArticleLimitedToFour(Status.ACTIVE, pageable);
        communityHomeRes.setPopularArticle(
                articleList.stream()
                .map(PopularArticleDto::toPopularArticleDto)
                .collect(Collectors.toList())
        );

        List<MainMission> missionList = mainMissionRepository.findProgressMissionByStatus(Status.ACTIVE);
        communityHomeRes.setMainMission(missionList
                .stream()
                .map(MainMissionListDto::tomainMissionListDto)
                .collect(Collectors.toList()));

        return communityHomeRes;
    }
}
