package rabbit.umc.com.demo.schedule.service;

import org.joda.time.DateTime;
import org.springframework.data.domain.Pageable;
import rabbit.umc.com.config.BaseException;
import rabbit.umc.com.config.BaseResponse;
import rabbit.umc.com.demo.schedule.dto.*;

import java.time.YearMonth;
import java.util.List;

public interface ScheduleService {
    ScheduleHomeRes getHome(Long userId);

    ScheduleDetailRes getScheduleDetail(Long scheduleId, Long userId) throws BaseException;
    Long postSchedule(PostScheduleReq postScheduleReq,Long userId) throws BaseException;

    void deleteSchedule(List<Long> scheduleIds,Long userId) throws BaseException;

    List<ScheduleListDto> getScheduleByWhen(String when, long userId);

    void updateSchedule(PostScheduleReq postScheduleReq, Long userId, Long scheduleId) throws BaseException;

    DayRes getScheduleWhenMonth(YearMonth yearMonth, long userId) throws BaseException;
}
