package rabbit.umc.com.demo.mainmission.dto;

import lombok.*;
import rabbit.umc.com.demo.mainmission.domain.MainMission;

import java.time.LocalDate;
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
        LocalDate currentDateTime = LocalDate.now();
        LocalDate endDateTime = mainMission.getEndAt();
        long daysRemaining = ChronoUnit.DAYS.between(currentDateTime, endDateTime);
        String dDay;
        if (daysRemaining >= 0) {
            dDay =  daysRemaining + "일";
        }  else {
            dDay = "미션 종료";
        }

        this.mainMissionId = mainMission.getId();
        this.mainMissionName = mainMission.getTitle();
        this.dDay = dDay;
        this.mainMissionContent = mainMission.getContent();

    }

    }




