package rabbit.umc.com.demo.mission.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
@NoArgsConstructor
public class GetMyMissionRes {

    private Long id;
    private String title;
    @Schema(example = "D+n")
    private String dDay;
    private int challengerCnt;
    private Long categoryId;
    private String image;
    @Schema(description = "도전한 기간")
    private Long during; // 도전한 기간

    public static GetMyMissionRes toMyMissions(Mission mission) {
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


        // 도전 기간
        long during = ChronoUnit.DAYS.between(mission.getStartAt().toLocalDate(),currentDateTime);

        return new GetMyMissionRes(
                mission.getId(),
                mission.getTitle(),
                dDay,
                mission.getMissionUsers().size(),
                mission.getCategory().getId(),
                mission.getCategory().getImage(),
                during
        );
    }
}
