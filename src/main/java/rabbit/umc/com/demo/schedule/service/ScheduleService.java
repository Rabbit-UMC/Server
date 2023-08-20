package rabbit.umc.com.demo.schedule.service;

import org.springframework.data.domain.Pageable;
import rabbit.umc.com.config.BaseException;
import rabbit.umc.com.demo.schedule.domain.MissionSchedule;
import rabbit.umc.com.demo.schedule.domain.Schedule;
import rabbit.umc.com.demo.schedule.dto.*;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface ScheduleService {
    ScheduleHomeRes getHome(Long userId, Pageable pageable);

    ScheduleDetailRes getScheduleDetail(Long scheduleId, Long userId) throws BaseException;
    Long postSchedule(PostScheduleReq postScheduleReq,Long userId) throws BaseException;

    void deleteSchedule(List<Long> scheduleIds,Long userId) throws BaseException;

    List<ScheduleListDto> getScheduleByWhen(String when, long userId);

    void updateSchedule(PostScheduleReq postScheduleReq, Long userId, Long scheduleId) throws BaseException;
}
