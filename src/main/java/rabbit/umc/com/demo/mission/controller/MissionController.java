package rabbit.umc.com.demo.mission.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import rabbit.umc.com.config.BaseException;
import rabbit.umc.com.config.BaseResponse;
import rabbit.umc.com.config.BaseResponseStatus;
import rabbit.umc.com.demo.mission.dto.*;
import rabbit.umc.com.demo.mission.service.MissionService;
import rabbit.umc.com.utils.JwtService;

import java.util.ArrayList;
import java.util.List;
@Api(tags = {"일반 미션 관련 Controller"})
@RestController
@RequestMapping("/app/mission")
@RequiredArgsConstructor
public class MissionController {

    private final MissionService missionService;
    private final JwtService jwtService;

    /**
     * 미션 홈
     */
    @ApiOperation(value = "일반 미션 리스트 조회하는 메소드")
    @GetMapping()
    public BaseResponse<List<MissionHomeRes>> getHome(){
        String token = jwtService.createJwt(102);
        System.out.println("token = " + token);
        List<MissionHomeRes> resultList = missionService.getMissionHome();
//        System.out.println("jwtService = " + jwtService.createJwt(1));

        return new BaseResponse<>(resultList);
    }

    /**
     * 미션 카테고리 별로 확인
     */
    @ApiOperation(value = "일반 미션 카테고리 별 리스트 조회하는 메소드")
    @GetMapping("category/{categoryId}")
    public BaseResponse<List<MissionHomeRes>> getHomeByCategoryId(@PathVariable(name = "categoryId") Long categoryId){

        List<MissionHomeRes> resultList = missionService.getMissionByMissionCategoryId(categoryId);

        return new BaseResponse<>(resultList);
    }

    /**
     * 미션 생성
     */
    @ApiOperation(value = "일반 미션 생성 메소드")
    @PostMapping()
    public BaseResponse postMission(@RequestBody PostMissionReq postMissionReq){
        try {
//            System.out.println("jwtService.createJwt(1) = " + jwtService.createJwt(1));

            Long userId = (long) jwtService.getUserIdx();
            missionService.postMission(postMissionReq,userId);
            return new BaseResponse<>("미션 생성 완료");
        } catch (BaseException e) {
            return new BaseResponse(e.getStatus());
        }


    }

    /**
     * 미션 성공 리스트 페이지
     */
    @ApiOperation(value = "일반 미션 성공 리스트 조회 메소드")
    @GetMapping("/success")
    public BaseResponse<MissionHistoryRes> getSuccessMissions(){
        try {
            Long userId = (long) jwtService.getUserIdx();
            MissionHistoryRes result = missionService.getSuccessMissions(userId);
            return new BaseResponse<>(result);
        } catch (BaseException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * 미션 실패 리스트
     */
    @ApiOperation(value = "일반 미션 실패 리스트 조회 메소드")
    @GetMapping("/failures")
    public BaseResponse<MissionHistoryRes> getFailureMissions(){
        try {
            Long userId = (long) jwtService.getUserIdx();
            MissionHistoryRes result = missionService.getFailureMissions(userId);
            return new BaseResponse<>(result);
        } catch (BaseException e) {
            return new BaseResponse(e.getMessage());
        }
    }

    /**
     * 도전중인 미션 리스트
     */
    @ApiOperation(value = "도전중인 일반 미션 리스트 조회 메소드")
    @GetMapping("/my-missions")
    public BaseResponse<List<GetMyMissionRes>> getMyMissions(){
        try {
            long userId = (long) jwtService.getUserIdx();
            List<GetMyMissionRes> resultList = missionService.getMyMissions(userId);

            return new BaseResponse<>(resultList);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }

    }

    /**
     * 미션 상세보기
     */
    @ApiOperation(value = "일반 미션 상세 정보 조회 메소드")
    @GetMapping("/{missionId}")
    public BaseResponse<GetMissionDetailDto> getMissionDetail(@PathVariable(name = "missionId") Long missionId){
        try {
            long userId = (long) jwtService.getUserIdx();
            GetMissionDetailDto getMissionDetailRes = missionService.getMissionDetail(missionId,userId);
            return new BaseResponse<>(getMissionDetailRes);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 도전중인 미션 리스트 상세
     */
    @ApiOperation(value = "도전중인 미션 상세 정보 조회 메소드")
    @GetMapping("/my-missions/{missionId}")
    public BaseResponse<GetMissionDetailDto> getMyMissionDetail(@PathVariable(name = "missionId") Long missionId) {
        try {
            long userId = (long) jwtService.getUserIdx();
            GetMissionDetailDto getMissionDeatilRes = missionService.getMyMissionDetail(userId,missionId);
            return new BaseResponse<>(getMissionDeatilRes);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 도전중인 미션 하위 일정
     */
    @ApiOperation(value = "도전중인 미션 하위 일정 조회 메소드")
    @GetMapping("/my-missions/schedule/{missionId}")
    public BaseResponse<List<GetMyMissionSchedule>> getMyMissionSchedules(@PathVariable(name = "missionId") long missionId){
        try {
            long userId = (long)jwtService.getUserIdx();
            List<GetMyMissionSchedule> resultList = missionService.getMyMissionSchedules(userId,missionId);
            return new BaseResponse<>(resultList);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     *  도전중인 미션 삭제
     */
    @ApiOperation(value = "도전중인 미션 삭제 메소드")
    @DeleteMapping("/my-missions/{missionsIds}")
    public BaseResponse deleteMyMission(@PathVariable List<Long> missionsIds){
        try {
            long userId = (long) jwtService.getUserIdx();
            missionService.deleteMyMissoin(missionsIds,userId);
            return new BaseResponse<>(missionsIds + " 미션 삭제 완료");
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus().getMessage());
        }
    }

    /**
     * 미션 신고
     */
    @ApiOperation(value = "일반 미션 삭제 메소드")
    @PostMapping("report/{missionId}")
    public BaseResponse reportMission(@PathVariable(name = "missionId") long missionId){
        try {
            long userId = (long) jwtService.getUserIdx();
            missionService.reportMission(missionId, userId);
            return new BaseResponse<>(missionId + "번 미션 신고됨");
        } catch (BaseException e) {
            return new BaseResponse(e.getStatus().getMessage());
        }
    }

    /**
     * 미션 같이하기
     */
    @ApiOperation(value = "일반 미션 같이하기 메소드")
    @PostMapping("/{missionId}")
    public BaseResponse togetherMission(@PathVariable(name = "missionId") long missionId){
        try {
            long userId = (long) jwtService.getUserIdx();
            missionService.togetherMission(missionId,userId);
            return new BaseResponse<>(missionId + " 미션 같이하기 성공");
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus().getMessage());
        }
    }

    /**
     *  미션 등록시 주제명 리스트
     */
    @ApiOperation(value = "미션 카테고리 리스트 조회 메소드")
    @GetMapping("/category")
    public BaseResponse<List<MissionCategoryRes>> getMissionCategoryList(){
        List<MissionCategoryRes> resultList = missionService.getMissionCategory();
        return new BaseResponse<>(resultList);
    }
}
