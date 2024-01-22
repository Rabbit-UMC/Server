package rabbit.umc.com.demo.schedule.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import rabbit.umc.com.config.BaseException;
import rabbit.umc.com.config.BaseResponse;
import rabbit.umc.com.config.BaseResponseStatus;
import rabbit.umc.com.demo.schedule.dto.*;
import rabbit.umc.com.demo.schedule.service.ScheduleService;
import rabbit.umc.com.utils.JwtService;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
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
    @Operation(summary = "일정 홈 조회 API")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "JWT4001", description = "JWT 토큰을 주세요!",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "JWT4002", description = "JWT 토큰 만료",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "JWT4003", description = "권한 없는 접근",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    @GetMapping()
    public BaseResponse<ScheduleHomeRes> getHome(){
        try {
            String token = jwtService.createJwt(1);
            System.out.println("jwtService = " + token);
            System.out.println("jwtService.createRefreshToken() = " + jwtService.createRefreshToken());
            System.out.println("token = " + token);

            long userId = (long) jwtService.getUserIdx();
            ScheduleHomeRes scheduleHomeRes = scheduleService.getHome(userId);

            return new BaseResponse<>(scheduleHomeRes);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 일정 상세 페이지
     */
    @ApiOperation(value = "일정 상세 페이지 조회 메소드")
    @Operation(summary = "일정 상세 페이지 조회 API")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "JWT4001", description = "JWT 토큰을 주세요!",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "JWT4002", description = "JWT 토큰 만료",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "JWT4003", description = "권한 없는 접근",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "SCHEDULE4001", description = "존재하지 않는 일정입니다.",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    @Parameters({
            @Parameter(name = "scheduleId", description = "일정 아이디"),
    })
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
    @Operation(summary = "날짜 별 일정 리스트 조회 API")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "JWT4001", description = "JWT 토큰을 주세요!",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "JWT4002", description = "JWT 토큰 만료",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "JWT4003", description = "권한 없는 접근",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON401", description = "입력 값을 확인해주세요.",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    @Parameters({
            @Parameter(name = "when", description = "날짜 정보",example = "yyyy-MM-dd"),
    })
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
    @Operation(summary = "월 별 일정 날짜 리스트 조회 API")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "JWT4001", description = "JWT 토큰을 주세요!",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "JWT4002", description = "JWT 토큰 만료",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "JWT4003", description = "권한 없는 접근",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON401", description = "입력 값을 확인해주세요.",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "SCHEDULE4003", description = "해당 일정이 없습니다.",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    @Parameters({
            @Parameter(name = "month", description = "날짜(월)", example = "yyyy-MM"),
    })
    @GetMapping("/month/{month}")
    public BaseResponse<DayRes> getScheduleWhenMonth(@PathVariable(name = "month") String month){

        if(!isRegexMonth(month)){
            return new BaseResponse<>(BaseResponseStatus.REQUEST_ERROR);
        }
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
            YearMonth yearMonth = YearMonth.parse(month, formatter);
            long userId = (long) jwtService.getUserIdx();
            DayRes results = scheduleService.getScheduleWhenMonth(yearMonth,userId);
            return new BaseResponse<>(results);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 일정 등록
     */
    @ApiOperation(value = "일정 등록 메소드")
    @Operation(summary = "일정 등록 API")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "JWT4001", description = "JWT 토큰을 주세요!",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "JWT4002", description = "JWT 토큰 만료",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "JWT4003", description = "권한 없는 접근",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "SCHEDULE4002", description = "종료 시간은 시작 시간보다 커야 합니다.",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "MISSION4006", description = "해당 미션에 대한 일정이 같은 날짜에 있습니다.",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "SCHEDULE4004", description = "미션 날짜 범위 안에 해당하는 일정 날짜를 입력해주세요",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    @Parameters({
            @Parameter(name = "postScheduleReq", description = "일정 정보"),
    })
    @PostMapping()
    public BaseResponse postSchedule(@RequestBody PostScheduleReq postScheduleReq){
        if(checkStartedTimeAndEndedTime(postScheduleReq.getStartAt(),postScheduleReq.getEndAt()))
            return new BaseResponse(FAILED_TO_POST_SCHEDULE_DATE);
        try {
            Long userId = (long) jwtService.getUserIdx();
            Long scheduleId = scheduleService.postSchedule(postScheduleReq,userId);
            return new BaseResponse<>(scheduleId);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     *  일정 삭제
     */
    @ApiOperation(value = "일정 삭제 메소드")
    @Operation(summary = "일정 삭제 API")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "JWT4001", description = "JWT 토큰을 주세요!",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "JWT4002", description = "JWT 토큰 만료",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "JWT4003", description = "권한 없는 접근",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "SCHEDULE4001", description = "존재하지 않는 일정입니다.",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    @Parameters({
            @Parameter(name = "postScheduleReq", description = "일정 정보"),
    })
    @DeleteMapping("/{scheduleIds}")
    public BaseResponse deleteSchedule(@PathVariable List<Long> scheduleIds){
        try {
            Long userId = (long) jwtService.getUserIdx();
            scheduleService.deleteSchedule(scheduleIds,userId);
        } catch (BaseException e) {
            return new BaseResponse(e.getStatus());
        }
        return new BaseResponse<>(scheduleIds + "번 일정 삭제됨");
    }



    /**
     *  일정 수정
     */
    @ApiOperation(value = "일정 수정 메소드")
    @Operation(summary = "일정 수정 API")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "JWT4001", description = "JWT 토큰을 주세요!",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "JWT4002", description = "JWT 토큰 만료",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "JWT4003", description = "권한 없는 접근",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "SCHEDULE4002", description = "종료 시간은 시작 시간보다 커야 합니다.",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "SCHEDULE4001", description = "존재하지 않는 일정입니다.",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    @Parameters({
            @Parameter(name = "postScheduleReq", description = "일정 정보"),
            @Parameter(name = "scheduleId", description = "일정 아이디"),
    })
    @PatchMapping("/{scheduleId}")
    public BaseResponse patchSchedule(@PathVariable(name = "scheduleId") Long scheduleId,@RequestBody PostScheduleReq postScheduleReq) {
        if(checkStartedTimeAndEndedTime(postScheduleReq.getStartAt(),postScheduleReq.getEndAt()))
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
