package rabbit.umc.com.demo.schedule.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
public class ScheduleHomeRes {
    private List<MissionListDto> missionList;
    private List<ScheduleListDto> scheduleList;

}
