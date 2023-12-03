package rabbit.umc.com.demo.user.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import rabbit.umc.com.demo.mission.Mission;
import rabbit.umc.com.demo.mission.dto.MissionHomeRes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
@Getter
@Setter
@AllArgsConstructor
public class UserMissionResDto {
    private Long missionId;
    private String title;
//    private String content;
    private String startAt;
    private String endAt;
    private Long categoryId;
    private String image;
    private int challengerCnt;
    private int successCnt;

    public static UserMissionResDto toUserMissionResDto (Mission mission){

        String startAt = mission.getStartAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String endAt = mission.getEndAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

//        System.out.println("mission = " + mission.getMissionUsers().size());
//        LocalDate targetDateTime = mission.getStartAt().toLocalDate();
//        LocalDate currentDateTime = LocalDateTime.now().toLocalDate();
//        long daysUntilTarget = ChronoUnit.DAYS.between(currentDateTime, targetDateTime); // 현재 날짜와 대상 날짜 사이의 일 수 계산

        return new UserMissionResDto(
                mission.getId(),
                mission.getTitle(),
//                mission.getContent(),
                startAt,
                endAt,
                mission.getCategory().getId(),
                mission.getCategory().getImage(),
                mission.getMissionUsers().size(),
                mission.getMissionUserSuccessList().size()
        );
    }
}
