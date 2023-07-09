package rabbit.umc.com.demo.mainmission.dto;

import lombok.*;
import rabbit.umc.com.demo.mainmission.domain.MainMission;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
@Data
@NoArgsConstructor
public class MainMissionListDto {
    private Long mainMissionId;
    private String mainMissionTitle;
    private Timestamp endTime;
    private String categoryImage;
    private String categoryName;



    public static MainMissionListDto tomainMissionListDto(MainMission mainMission){
        return new MainMissionListDto(
                mainMission.getId(),
                mainMission.getTitle(),
                mainMission.getEndAt(),
                mainMission.getCategory().getImage(),
                mainMission.getCategory().getName()
        );
    }

}
