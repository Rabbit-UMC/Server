package rabbit.umc.com.demo.schedule.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import rabbit.umc.com.demo.schedule.domain.Schedule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Setter
public class DayRes {
    private Map<Integer,Integer> schedulesOfDay = new HashMap<>();

    public void setSchedulesOfDay(int dayOfMonth, Integer cnt) {
        this.schedulesOfDay.put(dayOfMonth,cnt);
    }


}
