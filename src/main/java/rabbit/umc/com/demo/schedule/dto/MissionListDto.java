package rabbit.umc.com.demo.schedule.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rabbit.umc.com.demo.mission.Mission;


import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MissionListDto {
    // 미션
    private Long missionId;
    private String missionTitle;

    // 미션 도전자수 미션 테이블 미션 유저 테이블 조인 해서 count
    private int challengerCnts;

    // 종료날짜 - 오늘 날짜
    @Schema(example = "D+n")
    private String dDAy;

    public static MissionListDto toMissionListDto(Mission mission){

//        LocalDate targetDateTime = mission.getEndAt().toLocalDate();
        LocalDate targetDateTime = mission.getStartAt().toLocalDate();
        LocalDate currentDateTime = LocalDateTime.now().toLocalDate();
        long daysUntilTarget = ChronoUnit.DAYS.between(currentDateTime, targetDateTime); // 현재 날짜와 대상 날짜 사이의 일 수 계산
        String dDay;
        if (daysUntilTarget > 0) {
            dDay = "D-" + daysUntilTarget;
        } else if (daysUntilTarget == 0) {
            dDay = "D-day";
        } else {
            dDay = "D+" + Math.abs(daysUntilTarget);
        }

        return new MissionListDto(
            mission.getId(),
            mission.getTitle(),
            mission.getMissionUsers().size(),
            dDay
        );
    }

}
