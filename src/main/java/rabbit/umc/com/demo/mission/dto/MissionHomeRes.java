package rabbit.umc.com.demo.mission.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rabbit.umc.com.demo.mission.Mission;

import java.sql.Date;
import java.text.SimpleDateFormat;

@Getter
@Setter
@AllArgsConstructor
public class MissionHomeRes {

    private Long missionId;
    private String title;
    private String content;
    private int challengerCnt;
    private String startAt;
    private String endAt;
    private String image;

    private Long categoryId;

    public static MissionHomeRes toMissionHomeRes(Mission mission){
        Date startTime = new Date(mission.getStartAt().getTime());
        Date endTime = new Date(mission.getEndAt().getTime());
        SimpleDateFormat sdf = new SimpleDateFormat("MM.dd");
        String startAt = sdf.format(startTime);
        String endAt = sdf.format(endTime);

        return new MissionHomeRes(
                mission.getId(),
                mission.getTitle(),
                mission.getContent(),
                mission.getMissionUsers().size(),
                startAt,
                endAt,
                mission.getImage(),
                mission.getCategory().getId()
        );
    }
}
