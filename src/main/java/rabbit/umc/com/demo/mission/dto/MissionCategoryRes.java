package rabbit.umc.com.demo.mission.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import rabbit.umc.com.demo.mission.MissionCategory;

@Getter
@Setter
@AllArgsConstructor
public class MissionCategoryRes {
    private Long id;
    private String title;

    public static MissionCategoryRes toMissionCategoryRes(MissionCategory missionCategory) {
        return new MissionCategoryRes(
                missionCategory.getId(),
                missionCategory.getTitle()
        );
    }
}
