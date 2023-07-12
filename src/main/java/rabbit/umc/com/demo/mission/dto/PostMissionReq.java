package rabbit.umc.com.demo.mission.dto;

import lombok.Getter;
import lombok.Setter;
import rabbit.umc.com.config.BaseTimeEntity;

@Getter
@Setter
public class PostMissionReq extends BaseTimeEntity {
    private String title;
    private String content;
    private Long categoryId;
    private String startAt;
    private String endAt;
    private int isOpen;

}
