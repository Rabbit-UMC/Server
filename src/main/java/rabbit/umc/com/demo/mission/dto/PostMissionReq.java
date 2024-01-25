package rabbit.umc.com.demo.mission.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import rabbit.umc.com.demo.base.BaseTimeEntity;

@Getter
@Setter
public class PostMissionReq extends BaseTimeEntity {
    private String title;
    private String content;
    @Schema(description = "1 : 자유, 2 : 운동, 3: 예술")
    private Long categoryId;
    @Schema(example = "yyyy-MM-dd")
    private String startAt;
    @Schema(example = "yyyy-MM-dd")
    private String endAt;
    @Schema(description = "0이 공개 1이 비공개")
    private int isOpen;

}
