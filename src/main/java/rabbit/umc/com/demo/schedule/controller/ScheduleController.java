package rabbit.umc.com.demo.schedule.controller;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import rabbit.umc.com.config.BaseException;
import rabbit.umc.com.config.BaseResponse;
import rabbit.umc.com.config.BaseResponseStatus;
import rabbit.umc.com.demo.schedule.domain.MissionSchedule;
import rabbit.umc.com.demo.schedule.domain.Schedule;
import rabbit.umc.com.demo.schedule.dto.*;
import rabbit.umc.com.demo.schedule.service.ScheduleService;
import rabbit.umc.com.utils.JwtService;

import javax.validation.Valid;
import javax.validation.constraints.Negative;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static rabbit.umc.com.config.BaseResponseStatus.FAILED_TO_POST_SCHEDULE_DATE;
import static rabbit.umc.com.utils.ValidationRegex.checkStartedAtAndEndedAt;
import static rabbit.umc.com.utils.ValidationRegex.isRegexDate;

@RestController
@RequestMapping("/app/schedule")
@RequiredArgsConstructor
@Slf4j
//@Validated
public class ScheduleController {

    private final ScheduleService scheduleService;
    private final JwtService jwtService;


    /**
     *
     * 일정 홈 화면
     */
    @GetMapping()
    public BaseResponse<ScheduleHomeRes> getHome(Pageable pageable){
        try {
            jwtService.createJwt(1);
            long userId = (long) jwtService.getUserIdx();
            ScheduleHomeRes scheduleHomeRes = scheduleService.getHome(userId, pageable);
            return new BaseResponse<>(scheduleHomeRes);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }

    }

    /**
     * 일정 상세 페이지
     */
    @GetMapping("/{scheduleId}")
    public BaseResponse<ScheduleDetailRes> getScheduleDetail(@PathVariable("scheduleId") Long scheduleId){

        try {
            Long userId = (long) jwtService.getUserIdx();
            ScheduleDetailRes scheduleDetailRes = scheduleService.getScheduleDetail(scheduleId, userId);
            return new BaseResponse<ScheduleDetailRes>(scheduleDetailRes);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 날짜 별 일정 리스트 조회
     */
    @GetMapping("/when/{when}")
    public BaseResponse<List<ScheduleListDto>> getScheduleByWhen(@PathVariable(name = "when") String when) {
        
        if(isRegexDate(when)){
            return new BaseResponse<>(BaseResponseStatus.REQUEST_ERROR);
        }else {
            try {
                long userId = (long)jwtService.getUserIdx();
                List<ScheduleListDto> resultList = scheduleService.getScheduleByWhen(when,userId);
                return new BaseResponse<>(resultList);
            } catch (BaseException e) {
                return new BaseResponse<>(e.getStatus());
            }
        }


    }

    /**
     * 일정 등록
     */
    @PostMapping()
    public BaseResponse postSchedule(@RequestBody PostScheduleReq postScheduleReq){
        if(checkStartedAtAndEndedAt(postScheduleReq.getStartAt(),postScheduleReq.getEndAt()))
            return new BaseResponse(FAILED_TO_POST_SCHEDULE_DATE);
        try {
            Long userId = (long) jwtService.getUserIdx();
            Long scheduleId = scheduleService.postSchedule(postScheduleReq,userId);
            return new BaseResponse<>(scheduleId);
        } catch (BaseException e) {
            return new BaseResponse<>(BaseResponseStatus.FAILED_TO_POST_SCHEDULE);
        }
    }

    /**
     *  일정 삭제
     */
    @DeleteMapping("/{scheduleIds}")
    public BaseResponse deleteSchedule(@PathVariable List<Long> scheduleIds){
        try {
            Long userId = (long) jwtService.getUserIdx();
            scheduleService.deleteSchedule(scheduleIds,userId);
        } catch (BaseException e) {
            return new BaseResponse(BaseResponseStatus.FAILED_TO_SCHEDULE);
        }
        return new BaseResponse<>(scheduleIds + "번 일정 삭제됨");
    }



    /**
     *  일정 수정
     */
    @PatchMapping("/{scheduleId}")
    public BaseResponse patchSchedule(@PathVariable(name = "scheduleId") Long scheduleId,@RequestBody PostScheduleReq postScheduleReq) {
        if(checkStartedAtAndEndedAt(postScheduleReq.getStartAt(),postScheduleReq.getEndAt()))
            return new BaseResponse(FAILED_TO_POST_SCHEDULE_DATE);

        try {
            Long userId = (long) jwtService.getUserIdx();
            scheduleService.updateSchedule(postScheduleReq,userId,scheduleId);
        } catch (BaseException e) {
            return new BaseResponse(e.getStatus());
        }

        return new BaseResponse<>(scheduleId + "번 일정 수정됨");
    }
}
