package rabbit.umc.com.demo.mainmission;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import rabbit.umc.com.config.BaseException;
import rabbit.umc.com.config.BaseResponse;
import rabbit.umc.com.demo.mainmission.dto.GetMainMissionRes;
import rabbit.umc.com.utils.JwtService;

@RestController
@RequiredArgsConstructor
public class MainMissionController {
    private final MainMissionService mainMissionService;
    private final JwtService jwtService;

    /**
     * 메인 미션 상세 조회
     * @param mainMissionId
     * @return
     * @throws BaseException
     */
    @GetMapping("/app/main-mission/{mainMissionId}")
    public BaseResponse<GetMainMissionRes> getMainMission(@PathVariable("mainMissionId") Long mainMissionId) throws BaseException{
        try {
            GetMainMissionRes getMainMissionRes = mainMissionService.getMainMission(mainMissionId);

            return new BaseResponse<>(getMainMissionRes);
        }catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 메인 미션 인증 사진 좋아요
     * @param mainMissionProofId
     * @return
     * @throws BaseException
     */
    @PostMapping("/app/main-mission/proof/{mainMissionProofId}/like")
    public BaseResponse likeMissionProof(@PathVariable("mainMissionProofId")Long mainMissionProofId) throws BaseException{
        try {
            System.out.println(jwtService.createJwt(1));
            Long userId = (long) jwtService.getUserIdx();
            mainMissionService.likeMissionProof(userId, mainMissionProofId);
            return new BaseResponse<>(mainMissionProofId + "번 사진 좋아요");
        }catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 메인 미션 인증 사진 좋아요 취소
     * @param mainMissionProofId
     * @return
     * @throws BaseException
     */
    @DeleteMapping("/app/main-mission/proof/{mainMissionProofId}/unlike")
    public BaseResponse unLikeMissionProof(@PathVariable("mainMissionProofId")Long mainMissionProofId) throws BaseException{
        try {
            System.out.println(jwtService.createJwt(1));
            Long userId = (long) jwtService.getUserIdx();
            mainMissionService.unLikeMissionProof(userId, mainMissionProofId);
            return new BaseResponse<>(mainMissionProofId+ "번 좋아요 취소");
        }catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }

    }

    /**
     * 메인 미션 인증 사진 신고
     * @param mainMissionProofId
     * @return
     * @throws BaseException
     */
    @PostMapping("/app/main-mission/proof/{mainMissionProofId}/report")
    public BaseResponse reportMissionProof(@PathVariable("mainMissionProofId") Long mainMissionProofId) throws BaseException{
        try {
            System.out.println(jwtService.createJwt(1));
            Long userId = (long) jwtService.getUserIdx();
            mainMissionService.reportMissionProof(userId, mainMissionProofId);
            return new BaseResponse<>(mainMissionProofId + "번 신고 완료되었습니다.");

        }catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

}
