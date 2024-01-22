package rabbit.umc.com.demo.schedule.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.cglib.core.Local;
import rabbit.umc.com.demo.schedule.domain.Schedule;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@AllArgsConstructor
public class ScheduleListDto {
    //일정
    private Long scheduleId;
    private String scheduleTitle; // 일정 이름
    @Schema(example = "HH:mm")
    private String scheduleStart; // 시작 시간
    @Schema(example = "HH:mm")
    private String scheduleEnd; // 종료 시간
    @Schema(example = "yyyy-MM-dd")
    private String scheduleWhen; // 일정 날짜

    public static ScheduleListDto toScheduleDto(Schedule schedule) {

        String when = schedule.getStartAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String startTime = schedule.getStartAt().format(DateTimeFormatter.ofPattern("HH:mm"));
        String endTime = schedule.getEndAt().format(DateTimeFormatter.ofPattern("HH:mm"));

        return new ScheduleListDto(
                schedule.getId(),
                schedule.getTitle(),
                startTime,
                endTime,
               when);
    }

}
