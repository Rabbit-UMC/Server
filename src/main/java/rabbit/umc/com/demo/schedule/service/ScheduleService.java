package rabbit.umc.com.demo.schedule.service;

import rabbit.umc.com.demo.schedule.domain.Schedule;
import rabbit.umc.com.demo.schedule.dto.PostScheduleReq;
import rabbit.umc.com.demo.schedule.dto.ScheduleDetailRes;
import rabbit.umc.com.demo.schedule.dto.ScheduleHomeRes;
import rabbit.umc.com.demo.schedule.dto.ScheduleListDto;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface ScheduleService {
    ScheduleHomeRes getHome();

    ScheduleDetailRes getScheduleDetail(Long scheduleId);
    Long postSchedule(PostScheduleReq postScheduleReq,Long userId);

    void deleteSchedule(Long scheduleId);

    void patchSchedule(Schedule schedule);

    Schedule findById(Long scheduleId);

    List<ScheduleListDto> getScheduleByWhen(String when) throws ParseException;
}
