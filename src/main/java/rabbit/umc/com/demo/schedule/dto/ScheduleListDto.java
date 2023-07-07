package rabbit.umc.com.demo.schedule.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import rabbit.umc.com.demo.schedule.Schedule;

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
    private Timestamp sceduleStart; // 시작 시간
    private Timestamp sceduleEnd; // 종료 시간
    private String scheduleWhen; // 일정 날짜

    public static ScheduleListDto toScheduleDto(Schedule schedule) {
        Date date = new Date(schedule.getStartAt().getTime());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String sw = sdf.format(date);

        return new ScheduleListDto(
                schedule.getId(),
                schedule.getTitle(),
                schedule.getStartAt(),
                schedule.getEndAt(),
               sw);
    }

}
