package rabbit.umc.com.demo.community.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import rabbit.umc.com.demo.mainmission.dto.MainMissionListDto;
import java.util.List;

@Getter
@Setter
@Data
public class CommunityHomeRes {


    private List<MainMissionListDto> mainMission;
    private List<PopularArticleDto> popularArticle;

}