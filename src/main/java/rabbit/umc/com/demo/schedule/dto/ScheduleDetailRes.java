package rabbit.umc.com.demo.schedule.dto;

import lombok.*;
import rabbit.umc.com.demo.schedule.domain.MissionSchedule;
import rabbit.umc.com.demo.schedule.domain.Schedule;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

@Getter
@Setter
@AllArgsConstructor
public class ScheduleDetailRes {
    private Long id;
    private String scheduleTitle;
    private String missionTitle;
    private String startAt;
    private String endAt;
    private String when;
    private String content;

    private Long missionId;


    public static ScheduleDetailRes setMissionSchedule(MissionSchedule missionSchedule) {
        Date startTime = new Date(missionSchedule.getSchedule().getStartAt().getTime());
        Date endTime = new Date(missionSchedule.getSchedule().getEndAt().getTime());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String when = sdf.format(startTime);

        SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm");
        String startAt = sdf2.format(startTime);
        String endAt = sdf2.format(endTime);
        return new ScheduleDetailRes(
                missionSchedule.getSchedule().getId(),
                missionSchedule.getSchedule().getTitle(),
                missionSchedule.getMission().getTitle(),
                startAt,
                endAt,
                when,
                missionSchedule.getSchedule().getContent(),
                missionSchedule.getMission().getId()
        );
    }
}
