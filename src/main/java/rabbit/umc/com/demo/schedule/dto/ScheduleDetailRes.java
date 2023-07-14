package rabbit.umc.com.demo.schedule.dto;

import lombok.*;
import rabbit.umc.com.demo.schedule.domain.MissionSchedule;
import rabbit.umc.com.demo.schedule.domain.Schedule;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
        String when = missionSchedule.getSchedule().getStartAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String startTime = missionSchedule.getSchedule().getStartAt().format(DateTimeFormatter.ofPattern("HH:mm"));
        String endTime = missionSchedule.getSchedule().getEndAt().format(DateTimeFormatter.ofPattern("HH:mm"));

        // 일정에 미션이 있을 때
        if(missionSchedule.getMission() != null){
            return new ScheduleDetailRes(
                    missionSchedule.getSchedule().getId(),
                    missionSchedule.getSchedule().getTitle(),
                    missionSchedule.getMission().getTitle(),
                    startTime,
                    endTime,
                    when,
                    missionSchedule.getSchedule().getContent(),
                    missionSchedule.getMission().getId()
            );
        }else {
            // 일정에 미션이 없을 때
            return new ScheduleDetailRes(
                    missionSchedule.getSchedule().getId(),
                    missionSchedule.getSchedule().getTitle(),
                    null,
                    startTime,
                    endTime,
                    when,
                    missionSchedule.getSchedule().getContent(),
                    null
            );
        }


    }
}
