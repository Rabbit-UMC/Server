package rabbit.umc.com.demo.schedule.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import rabbit.umc.com.config.BaseResponse;
import rabbit.umc.com.demo.schedule.Schedule;
import rabbit.umc.com.demo.schedule.dto.ScheduleHomeRes;
import rabbit.umc.com.demo.schedule.dto.ScheduleListDto;
import rabbit.umc.com.demo.schedule.service.ScheduleService;

import java.util.List;

@RestController
@RequestMapping("/app/schedule")
@Slf4j
public class ScheduleController {

    private final ScheduleService scheduleService;

    @Autowired
    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }



    @ResponseBody
    @GetMapping()
    public BaseResponse<ScheduleHomeRes> getHome(){
        ScheduleHomeRes scheduleHomeRes = scheduleService.getHome();

        System.out.println("시작");
        System.out.println("scheduleHomeRes = " + scheduleHomeRes);
        System.out.println("종료");

        return new BaseResponse<>(scheduleHomeRes);
    }
}
