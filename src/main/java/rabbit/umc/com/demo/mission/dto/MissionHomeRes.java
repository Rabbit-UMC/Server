package rabbit.umc.com.demo.mission.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rabbit.umc.com.demo.mission.Mission;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@Getter
@Setter
@AllArgsConstructor
public class MissionHomeRes {

    private Long missionId;
    private String title;
    private String content;
    private int challengerCnt;
    @Schema(example = "yyyy-MM-dd")
    private String startAt;
    @Schema(example = "yyyy-MM-dd")
    private String endAt;
    private Long categoryId;
    private String image;
    private int successCnt;
    @Schema(example = "D+n")
    private String dDay;

    public static MissionHomeRes toMissionHomeRes(Mission mission){

        String startAt = mission.getStartAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String endAt = mission.getEndAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

//        System.out.println("mission = " + mission.getMissionUsers().size());
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

        return new MissionHomeRes(
                mission.getId(),
                mission.getTitle(),
                mission.getContent(),
                mission.getMissionUsers().size(),
                startAt,
                endAt,
                mission.getCategory().getId(),
                mission.getCategory().getImage(),
                mission.getMissionUserSuccessList().size(),
                dDay
        );
    }

}
