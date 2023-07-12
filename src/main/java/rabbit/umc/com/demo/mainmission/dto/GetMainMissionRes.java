package rabbit.umc.com.demo.mainmission.dto;

import lombok.*;
import rabbit.umc.com.demo.mainmission.domain.MainMission;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetMainMissionRes {
    private Long mainMissionId;
    private String mainMissionName;
    private String dDay;
    private String mainMissionContent;
    private List<RankDto> rank;
    private List<MissionProofImageDto> missionProofImages;

    public  GetMainMissionRes(MainMission mainMission){
        LocalDateTime currentDateTime = LocalDateTime.now().withMinute(0).withSecond(0);
        LocalDateTime endDateTime = mainMission.getEndAt().withMinute(0).withSecond(0);
        long daysRemaining = ChronoUnit.DAYS.between(currentDateTime.toLocalDate(), endDateTime.toLocalDate());
        String dDay;
        if (daysRemaining > 0) {
            dDay = "D-" + daysRemaining;
        } else if (daysRemaining == 0) {
            dDay = "D-day";
        } else {
            dDay = "D+" + Math.abs(daysRemaining);
        }

        this.mainMissionId = mainMission.getId();
        this.mainMissionName = mainMission.getTitle();
        this.dDay = dDay;
        this.mainMissionContent = mainMission.getContent();

    }

    }




