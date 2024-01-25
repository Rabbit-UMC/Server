package rabbit.umc.com.demo.schedule.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import rabbit.umc.com.demo.base.BaseTimeEntity;

@Getter
@Setter
public class PostScheduleReq extends BaseTimeEntity {
    private String content;
    @Schema(example = "HH:mm")
    private String endAt;
    @Schema(example = "HH:mm")
    private String startAt;
    @Schema(example = "yyyy-MM-dd")
    private String when;
    private String title;
    private Long missionId;
}
