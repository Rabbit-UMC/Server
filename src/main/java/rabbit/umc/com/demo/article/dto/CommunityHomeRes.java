package rabbit.umc.com.demo.article.dto;

import lombok.Getter;
import lombok.Setter;

import rabbit.umc.com.demo.mainmission.dto.MainMissionListDto;




import java.util.List;

@Getter
@Setter
public class CommunityHomeRes {



    private List<MainMissionListDto> mainMission;
    private List<PopularArticleDto> popularArticle;

}
