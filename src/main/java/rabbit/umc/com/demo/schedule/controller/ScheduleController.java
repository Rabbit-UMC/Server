package rabbit.umc.com.demo.schedule.controller;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import rabbit.umc.com.config.BaseException;
import rabbit.umc.com.config.BaseResponse;
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
        ScheduleHomeRes scheduleHomeRes = scheduleService.getHome();

        return new BaseResponse<>(scheduleHomeRes);
    }

    /**
     * 일정 상세 페이지
     */
    @GetMapping("/{scheduleId}")
    public BaseResponse<ScheduleDetailRes> getScheduleDetail(@PathVariable("scheduleId") Long scheduleId){
        ScheduleDetailRes scheduleDetailRes = scheduleService.getScheduleDetail(scheduleId);
        return new BaseResponse<ScheduleDetailRes>(scheduleDetailRes);
    }

    /**
     * 날짜 별 일정 리스트 조회
     */
    @GetMapping("/when")
    public BaseResponse<List<ScheduleListDto>> getScheduleByWhen(@RequestBody String when) throws ParseException {
        JsonObject jsonObject = JsonParser.parseString(when).getAsJsonObject();
        String whenStr = jsonObject.get("when").getAsString();
        System.out.println("when = " + when);
        List<ScheduleListDto> resultList = scheduleService.getScheduleByWhen(whenStr);
        return new BaseResponse<>(resultList);
    }

    /**
     * 일정 등록(미완성)
     */
    @PostMapping()
    public BaseResponse postSchedule(@RequestBody PostScheduleReq postScheduleReq) throws BaseException {

//        Long userId = (long) jwtService.getUserIdx();
        Long userId = 1L;
        Long scheduleId = scheduleService.postSchedule(postScheduleReq,userId);

        return new BaseResponse<>(scheduleId);
    }

    /**
     *  일정 삭제
     */
    @DeleteMapping("/{scheduleId}")
    public BaseResponse deleteSchedule(@PathVariable(name = "scheduleId") Long scheduleId) throws BaseException{
        scheduleService.deleteSchedule(scheduleId);
        return new BaseResponse<>(scheduleId + "번 일정 삭제됨");
    }



    /**
     *  일정 수정(미완성)
     */
    @PatchMapping("/{scheduleId}")
    public BaseResponse patchSchedule(@PathVariable(name = "schedulId") Long scheduleId,@RequestBody PatchScheduleReq patchScheduleReq) throws BaseException {
        Long userId = (long) jwtService.getUserIdx();
        Schedule schedule = scheduleService.findById(scheduleId);
        //// 여기부터다시
        scheduleService.patchSchedule(schedule);
        return new BaseResponse<>(patchScheduleReq.getScheduleId() + "번 일정 수정됨");
    }
}
