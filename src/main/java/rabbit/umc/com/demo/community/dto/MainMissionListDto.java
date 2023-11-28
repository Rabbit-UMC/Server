package rabbit.umc.com.demo.community.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
//사용 X
public class MainMissionListDto {
    private Long mainMissionId;
    private String mainMissionName;
    private int endTime;
    private String categoryImage;
    private String categoryName;

}
