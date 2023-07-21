package rabbit.umc.com.demo.mission.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import rabbit.umc.com.config.BaseException;
import rabbit.umc.com.config.BaseResponse;
import rabbit.umc.com.config.BaseResponseStatus;
import rabbit.umc.com.demo.mission.dto.*;
import rabbit.umc.com.demo.mission.service.MissionService;
import rabbit.umc.com.utils.JwtService;

import java.util.List;

@RestController
@RequestMapping("/app/mission")
@RequiredArgsConstructor
public class MissionController {

    private final MissionService missionService;
    private final JwtService jwtService;

    /**
     * 미션 홈
     */
    @GetMapping()
    public BaseResponse<List<MissionHomeRes>> getHome(){

        List<MissionHomeRes> resultList = missionService.getMissionHome();


        return new BaseResponse<>(resultList);
    }

    /**
     * 미션 카테고리 별로 확인
     */
    @GetMapping("category/{categoryId}")
    public BaseResponse<List<MissionHomeRes>> getHomeByCategoryId(@PathVariable(name = "categoryId") Long categoryId){

        List<MissionHomeRes> resultList = missionService.getMissionByMissionCategoryId(categoryId);

        return new BaseResponse<>(resultList);
    }

    /**
     * 미션 생성
     */
    @PostMapping()
    public BaseResponse postMission(@RequestBody PostMissionReq postMissionReq){
        try {
            Long userId = (long) jwtService.getUserIdx();
            missionService.postMission(postMissionReq,userId);
            return new BaseResponse<>("미션 생성 완료");
        } catch (BaseException e) {
            throw new RuntimeException(e);
        }


    }

    /**
     * 미션 성공 리스트 페이지
     */
    @GetMapping("/success")
    public BaseResponse<List<MissionHomeRes>> getSuccessMissions(){
        try {
            Long userId = (long) jwtService.getUserIdx();
            List<MissionHomeRes> resultList = missionService.getSuccessMissions(userId);
            return new BaseResponse<>(resultList);
        } catch (BaseException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * 미션 실패 리스트
     */
    @GetMapping("/failures")
    public BaseResponse<List<MissionHomeRes>> getFailureMissions(){
        try {
            Long userId = (long) jwtService.getUserIdx();
            List<MissionHomeRes> resultList = missionService.getFailureMissions(userId);
            return new BaseResponse<>(resultList);
        } catch (BaseException e) {
            return new BaseResponse(e.getMessage());
        }
    }

    /**
     * 도전중인 미션 리스트
     */
    @GetMapping("/my-missions")
    public BaseResponse<List<GetMyMissionRes>> getMyMissions(){
        try {
            long userId = (long) jwtService.getUserIdx();
            List<GetMyMissionRes> resultList = missionService.getMyMissions(userId);
            return new BaseResponse<>(resultList);
        } catch (BaseException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * 미션 상세보기
     */
    @GetMapping("/{missionId}")
    public BaseResponse<GetMissionDetailDto> getMissionDetail(@PathVariable(name = "missionId") Long missionId){
        try {
            GetMissionDetailDto getMissionDeatilRes = missionService.getMissionDetail(missionId);
            return new BaseResponse<>(getMissionDeatilRes);
        } catch (BaseException e) {
            return new BaseResponse<>(BaseResponseStatus.FAILED_TO_MISSION);
        }
    }

    /**
     * 도전중인 미션 리스트 상세
     */
    @GetMapping("/my-missions/{missionId}")
    public BaseResponse<GetMissionDetailDto> getMyMissionDetail(@PathVariable(name = "missionId") Long missionId) {
        try {
            long userId = (long) jwtService.getUserIdx();
            GetMissionDetailDto getMissionDeatilRes = missionService.getMyMissionDetail(userId,missionId);
            return new BaseResponse<>(getMissionDeatilRes);
        } catch (BaseException e) {
            return new BaseResponse<>(BaseResponseStatus.FAILED_TO_MISSION);
        }
    }

    /**
     * 도전중인 미션 하위 일정
     */
    @GetMapping("/my-missions/schedule/{missionId}")
    public BaseResponse<List<GetMyMissionSchedule>> getMyMissionSchedules(@PathVariable(name = "missionId") long missionId){
        try {
            long userId = (long)jwtService.getUserIdx();
            List<GetMyMissionSchedule> resultList = missionService.getMyMissionSchedules(userId,missionId);
            return new BaseResponse<>(resultList);
        } catch (BaseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *  도전중인 미션 삭제
     */
    @DeleteMapping("/my-missions/{missionId}")
    public BaseResponse deleteMyMission(@PathVariable(name = "missionId") long missionId){
        try {
            long userId = (long) jwtService.getUserIdx();
            missionService.deleteMyMissoin(missionId,userId);
            return new BaseResponse<>("미션 삭제 완료");
        } catch (BaseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 미션 신고 (여기 예외 처리)
     */
    @PostMapping("report/{missionId}")
    public BaseResponse reportMission(@PathVariable(name = "missionId") long missionId){
        try {
            long userId = (long) jwtService.getUserIdx();
            missionService.reportMission(missionId, userId);
            return new BaseResponse<>(missionId + "번 미션 신고됨");
        } catch (Exception e) {
            return new BaseResponse(e.getMessage());
        }
    }

    /**
     * 미션 같이하기
     */
    @PostMapping("/{missionId}")
    public BaseResponse togetherMission(@PathVariable(name = "missionId") long missionId){
        System.out.println("jwt token : " + jwtService.createJwt(2));
        try {
            long userId = (long) jwtService.getUserIdx();
            missionService.togetherMission(missionId,userId);
            return new BaseResponse<>(missionId + "번 미션 같이하기 성공");
        } catch (BaseException e) {
            return new BaseResponse<>(BaseResponseStatus.FAILED_TO_TOGETHER_MISSION);
        }
    }

    /**
     *  미션 등록시 주제명 리스트
     */
    @GetMapping("/category")
    public BaseResponse<List<MissionCategoryRes>> getMissionCategoryList(){
        List<MissionCategoryRes> resultList = missionService.getMissionCategory();
        return new BaseResponse<>(resultList);
    }
}
