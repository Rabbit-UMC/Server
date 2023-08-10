package rabbit.umc.com.demo.schedule.controller;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import rabbit.umc.com.config.BaseException;
import rabbit.umc.com.config.BaseResponse;
import rabbit.umc.com.config.BaseResponseStatus;
import rabbit.umc.com.demo.schedule.domain.MissionSchedule;
import rabbit.umc.com.demo.schedule.domain.Schedule;
import rabbit.umc.com.demo.schedule.dto.*;
import rabbit.umc.com.demo.schedule.service.ScheduleService;
import rabbit.umc.com.utils.JwtService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/app/schedule")
@RequiredArgsConstructor
@Slf4j
public class ScheduleController {

    private final ScheduleService scheduleService;
    private final JwtService jwtService;


    /**
     *
     * 일정 홈 화면
     */
    @GetMapping()
    public BaseResponse<ScheduleHomeRes> getHome(){
        try {
            jwtService.createJwt(1);
            long userId = (long) jwtService.getUserIdx();
            ScheduleHomeRes scheduleHomeRes = scheduleService.getHome(userId);
            return new BaseResponse<>(scheduleHomeRes);
        } catch (BaseException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * 일정 상세 페이지
     */
    @GetMapping("/{scheduleId}")
    public BaseResponse<ScheduleDetailRes> getScheduleDetail(@PathVariable("scheduleId") Long scheduleId){
        ScheduleDetailRes scheduleDetailRes = null;
        try {
            Long userId = (long) jwtService.getUserIdx();
            scheduleDetailRes = scheduleService.getScheduleDetail(scheduleId, userId);
        } catch (BaseException e) {
            return new BaseResponse<>(BaseResponseStatus.FAILED_TO_SCHEDULE);
        }
        return new BaseResponse<ScheduleDetailRes>(scheduleDetailRes);
    }

    /**
     * 날짜 별 일정 리스트 조회
     */
    @GetMapping("/when/{when}")
    public BaseResponse<List<ScheduleListDto>> getScheduleByWhen(@PathVariable(name = "when") String when) throws ParseException {
        try {
            long userId = (long)jwtService.getUserIdx();

            List<ScheduleListDto> resultList = scheduleService.getScheduleByWhen(when,userId);
            return new BaseResponse<>(resultList);
        } catch (BaseException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * 일정 등록
     */
    @PostMapping()
    public BaseResponse postSchedule(@RequestBody PostScheduleReq postScheduleReq){
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
        try {
            Long userId = (long) jwtService.getUserIdx();
            scheduleService.updateSchedule(postScheduleReq,userId,scheduleId);
        } catch (BaseException e) {
            return new BaseResponse(BaseResponseStatus.FAILED_TO_SCHEDULE);
        }

        return new BaseResponse<>(scheduleId + "번 일정 수정됨");
    }
}
