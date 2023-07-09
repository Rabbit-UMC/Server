package rabbit.umc.com.demo.schedule.dto;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class PatchScheduleReq {
    private Long scheduleId;
    private String title;
    private Timestamp endAt;
    private Timestamp startAt;
    private Timestamp updatedAt;
    private String content;
    private Long missionId;
}
