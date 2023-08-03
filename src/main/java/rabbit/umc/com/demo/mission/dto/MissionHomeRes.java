package rabbit.umc.com.demo.mission.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rabbit.umc.com.demo.mission.Mission;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;

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
    private Long categoryId;
    private String image;

    private int successCnt;

    public static MissionHomeRes toMissionHomeRes(Mission mission){

        String startAt = mission.getStartAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String endAt = mission.getEndAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        return new MissionHomeRes(
                mission.getId(),
                mission.getTitle(),
                mission.getContent(),
                mission.getMissionUsers().size(),
                startAt,
                endAt,
                mission.getCategory().getId(),
                mission.getCategory().getImage(),
                mission.getMissionUserSuccessList().size()
        );
    }

}
