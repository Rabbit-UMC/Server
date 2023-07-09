package rabbit.umc.com.demo.schedule.dto;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class PostScheduleReq {
    private String content;
    private Timestamp createdAt;
    private Timestamp endAt;
    private Timestamp startAt;
    private String status;
    private String title;
    private Timestamp updatedAt;
    private Long missionId;
}
