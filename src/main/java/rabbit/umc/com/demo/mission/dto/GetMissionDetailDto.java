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
    private String image;
    private String startAt;
    private String endAt;
    private String content;
    private String categoryTitle;
    private Long categoryId;
    private boolean isAlreadyIn;

    public static GetMissionDetailDto toGetMissionDetaliDto(Mission mission, boolean isAlreadyIn){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String startAt = mission.getStartAt().format(formatter);
        String endAt = mission.getEndAt().format(formatter);
        return new GetMissionDetailDto(
                mission.getId(),
                mission.getTitle(),
                mission.getCategory().getImage(),
                startAt,
                endAt,
                mission.getContent(),
                mission.getCategory().getName(),
                mission.getCategory().getId(),
                isAlreadyIn
        );
    }
}
