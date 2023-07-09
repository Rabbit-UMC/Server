package rabbit.umc.com.demo.schedule.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import rabbit.umc.com.demo.schedule.domain.Schedule;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

@Getter
@Setter
@AllArgsConstructor
public class ScheduleListDto {
    //일정
    private Long sceduleId;
    private String sceduleTitle; // 일정 이름
    private String sceduleStart; // 시작 시간
    private String sceduleEnd; // 종료 시간
    private String scheduleWhen; // 일정 날짜

    public static ScheduleListDto toScheduleDto(Schedule schedule) {
        Date startTime = new Date(schedule.getStartAt().getTime());
        Date endTime = new Date(schedule.getEndAt().getTime());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String when = sdf.format(startTime);

        SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm");
        String startAt = sdf2.format(startTime);
        String endAt = sdf2.format(endTime);

        return new ScheduleListDto(
                schedule.getId(),
                schedule.getTitle(),
                startAt,
                endAt,
               when);
    }

}
