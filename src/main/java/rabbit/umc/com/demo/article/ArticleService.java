package rabbit.umc.com.demo.article;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rabbit.umc.com.demo.article.domain.Article;
import rabbit.umc.com.demo.article.dto.CommunityHomeRes;
import rabbit.umc.com.demo.article.dto.PopularArticle;
import rabbit.umc.com.demo.mainmission.MainMissionRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final MainMissionRepository mainMissionRepository;





    public CommunityHomeRes getHome() {
        CommunityHomeRes communityHomeRes = new CommunityHomeRes();
        List<Article> articleList = articleRepository.popularArticle();
        communityHomeRes.setPopularArticles(articleList.stream()
                .map(
                        PopularArticle::toPopularArticle)
                .collect(Collectors.toList())
        );
//        communityHomeRes.setMainMissions(mainMissionRepository.getReferenceById());

        return communityHomeRes;
    }
}
