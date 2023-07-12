package rabbit.umc.com.demo.mission.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import rabbit.umc.com.demo.mission.Mission;

import java.time.format.DateTimeFormatter;

@Getter
@Setter
@AllArgsConstructor
public class GetMissionDetailDto {
    private long id;
    private String title;
    private String startAt;
    private String endAt;
    private String content;
    private String categoryTitle;

    public static GetMissionDetailDto toGetMissionDetaliDto(Mission mission){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String startAt = mission.getStartAt().format(formatter);
        String endAt = mission.getEndAt().format(formatter);
        return new GetMissionDetailDto(
                mission.getId(),
                mission.getTitle(),
                startAt,
                endAt,
                mission.getContent(),
                mission.getMissionCategory().getTitle()

        );
    }
}
