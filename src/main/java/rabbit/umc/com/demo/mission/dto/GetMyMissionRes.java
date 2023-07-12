package rabbit.umc.com.demo.mission.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.GetMapping;
import rabbit.umc.com.demo.mission.Mission;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@Getter
@Setter
@AllArgsConstructor
public class GetMyMissionRes {

    private Long id;
    private String title;
    private String dDay;
    private int challengerCnt;
    private Long categoryId;

    public static GetMyMissionRes toMyMissions(Mission mission) {
        LocalDate targetDateTime = mission.getEndAt().toLocalDate();
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

        return new GetMyMissionRes(
                mission.getId(),
                mission.getTitle(),
                dDay,
                mission.getMissionUsers().size(),
                mission.getMissionCategory().getId()
        );
    }
}
