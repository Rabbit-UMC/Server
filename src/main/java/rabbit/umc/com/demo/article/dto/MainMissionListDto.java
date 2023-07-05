package rabbit.umc.com.demo.article.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MainMissionListDto {
    private Long mainMissionId;
    private String mainMissionName;
    private int endTime;
    private String categoryImage;
    private String categoryName;

}
