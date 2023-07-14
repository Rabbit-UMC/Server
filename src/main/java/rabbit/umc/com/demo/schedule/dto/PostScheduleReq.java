package rabbit.umc.com.demo.schedule.dto;

import lombok.Getter;
import lombok.Setter;
import rabbit.umc.com.config.BaseTimeEntity;

import java.sql.Timestamp;

@Getter
@Setter
public class PostScheduleReq extends BaseTimeEntity {
    private String content;
    private String endAt;
    private String startAt;
    private String status;
    private String title;
    private Long missionId;
}
