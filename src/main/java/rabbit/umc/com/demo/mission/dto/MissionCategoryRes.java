package rabbit.umc.com.demo.mission.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import rabbit.umc.com.demo.article.domain.Category;

@Getter
@Setter
@AllArgsConstructor
public class MissionCategoryRes {
    private Long id;
    private String title;

    public static MissionCategoryRes toMissionCategoryRes(Category category) {
        return new MissionCategoryRes(
                category.getId(),
                category.getName()
        );
    }
}
