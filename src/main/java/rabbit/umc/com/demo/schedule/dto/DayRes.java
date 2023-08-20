package rabbit.umc.com.demo.schedule.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import rabbit.umc.com.demo.schedule.domain.Schedule;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class DayRes {
    private List<Integer> dayList;

}
