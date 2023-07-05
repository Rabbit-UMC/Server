package rabbit.umc.com.demo.article.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CommunityHomeRes {

    private List<MainMissionListDto> mainMissions;
    private List<PopularArticle> PopularArticles;

}
