package rabbit.umc.com.demo.mainmission.dto;

import lombok.*;
import rabbit.umc.com.demo.mainmission.domain.MainMission;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@Getter
@Setter
@AllArgsConstructor
@Data
@NoArgsConstructor
public class MainMissionListDto {

    private Long mainMissionId;
    private String mainMissionTitle;
    private String categoryImage;
    private String categoryName;
    private String dDay;



    public static MainMissionListDto toMainMissionListDto(MainMission mainMission) {
        LocalDate currentDateTime = LocalDate.now();
        LocalDate endDateTime = mainMission.getEndAt();
        long daysRemaining = ChronoUnit.DAYS.between(currentDateTime, endDateTime);

        String dDay;
        if (daysRemaining > 0) {
            dDay = "D-" + daysRemaining;
        } else if (daysRemaining == 0) {
            dDay = "D-day";
        } else {
            dDay = "D+" + Math.abs(daysRemaining);
        }

        return new MainMissionListDto(
                mainMission.getId(),
                mainMission.getTitle(),
                mainMission.getCategory().getImage(),
                mainMission.getCategory().getName(),
                dDay
        );

    }

}
