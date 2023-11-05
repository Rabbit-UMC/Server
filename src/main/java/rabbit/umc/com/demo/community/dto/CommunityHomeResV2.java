package rabbit.umc.com.demo.community.dto;

import java.util.List;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import rabbit.umc.com.demo.mainmission.dto.MainMissionListDto;
import rabbit.umc.com.demo.mainmission.dto.MainMissionListDtoV2;

@Getter
@Setter
@Data
public class CommunityHomeResV2 {


    private List<MainMissionListDtoV2> mainMission;
    private List<PopularArticleDtoV2> popularArticle;
    private List<Long> userHostCategory;

}