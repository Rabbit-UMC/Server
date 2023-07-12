package rabbit.umc.com.demo.schedule.service;

import rabbit.umc.com.config.BaseException;
import rabbit.umc.com.demo.schedule.domain.MissionSchedule;
import rabbit.umc.com.demo.schedule.domain.Schedule;
import rabbit.umc.com.demo.schedule.dto.*;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface ScheduleService {
    ScheduleHomeRes getHome();

    ScheduleDetailRes getScheduleDetail(Long scheduleId);
    Long postSchedule(PostScheduleReq postScheduleReq,Long userId);

    void deleteSchedule(Long scheduleId,Long userId) throws BaseException;

    List<ScheduleListDto> getScheduleByWhen(String when) throws ParseException;

    void updateSchedule(PostScheduleReq postScheduleReq, Long userId, Long scheduleId);
}
