package rabbit.umc.com.demo.schedule.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import rabbit.umc.com.config.BaseException;
import rabbit.umc.com.config.BaseResponse;
import rabbit.umc.com.config.BaseResponseStatus;
import rabbit.umc.com.demo.schedule.dto.*;
import rabbit.umc.com.demo.schedule.service.ScheduleService;
import rabbit.umc.com.utils.JwtService;

import javax.validation.constraints.Min;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

import static rabbit.umc.com.config.BaseResponseStatus.FAILED_TO_POST_SCHEDULE_DATE;
import static rabbit.umc.com.utils.ValidationRegex.*;
@Api(tags = {"일정 관련 Controller"})
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
    @ApiOperation(value = "일정 홈 메소드")
    @GetMapping()
    public BaseResponse<ScheduleHomeRes> getHome(Pageable pageable){
        try {

            System.out.println(" = " + jwtService.createJwt(1));

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
    @ApiOperation(value = "일정 상세 페이지 조회 메소드")
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
    @ApiOperation(value = "날짜 별 일정 리스트 조회 메소드")
    @GetMapping("/when/{when}")
    public BaseResponse<List<ScheduleListDto>> getScheduleByWhen(@PathVariable(name = "when") String when) {
        if(!isRegexDate(when)){
            System.out.println("when = " + when);
            return new BaseResponse<>(BaseResponseStatus.REQUEST_ERROR);
        }else {
            try {
                long userId = (long)jwtService.getUserIdx();
                System.out.println("userId = " + userId);
                List<ScheduleListDto> resultList = scheduleService.getScheduleByWhen(when,userId);
                return new BaseResponse<>(resultList);
            } catch (BaseException e) {
                return new BaseResponse<>(e.getStatus());
            }
        }
    }

    /**
     * 월 별 일정 날짜 리스트
     */
    @ApiOperation(value = "월 별 일정 날짜 리스트 조회 메소드")
    @GetMapping("/month/{month}")
    public BaseResponse<DayRes> getScheduleWhenMonth(@PathVariable(name = "month") String month){
        System.out.println("month = " + month);
        System.out.println("isRegexMonth(month) = " + isRegexMonth(month));
        if(!isRegexMonth(month)){
            return new BaseResponse<>(BaseResponseStatus.REQUEST_ERROR);
        }

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
            YearMonth yearMonth = YearMonth.parse(month, formatter);
            long userId = (long) jwtService.getUserIdx();
            DayRes result = scheduleService.getScheduleWhenMonth(yearMonth,userId);
            return new BaseResponse<>(result);
        } catch (BaseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 일정 등록
     */
    @ApiOperation(value = "일정 등록 메소드")
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
    @ApiOperation(value = "일정 삭제 메소드")
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
    @ApiOperation(value = "일정 수정 메소드")
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
