package rabbit.umc.com.demo.schedule.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ScheduleHomeRes {
    private List<MissionListDto> missionList;
    private List<ScheduleListDto> sceduleList;
}
