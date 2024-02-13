package rabbit.umc.com.demo.mainmission.controller;

import static rabbit.umc.com.config.apiPayload.BaseResponseStatus.FAILED_TO_UPLOAD_PROOF_IMAGE;

import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import rabbit.umc.com.config.apiPayload.BaseException;
import rabbit.umc.com.config.apiPayload.BaseResponse;
import rabbit.umc.com.demo.mainmission.service.MainMissionService;
import rabbit.umc.com.demo.mainmission.dto.GetMainMissionRes;
import rabbit.umc.com.demo.mainmission.dto.MainMissionViewRes;
import rabbit.umc.com.demo.mainmission.dto.PostMainMissionReq;
import rabbit.umc.com.utils.JwtService;

@Api(tags = {"메인 미션 관련 Controller"})
@RestController
@RequiredArgsConstructor
@RequestMapping("/app")
public class MainMissionController {
    private final MainMissionService mainMissionService;
    private final JwtService jwtService;

    /**
     * 메인 미션 상세 조회
     * @param mainMissionId
     * @return
     * @throws BaseException
     */
    @Tag(name = "mainMissionView")
    @Operation(summary = "메인 미션 상세 조회 API")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "JWT4001", description = "JWT 토큰을 주세요!",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "JWT4002", description = "JWT 토큰 만료",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "MISSION4001", description = "메인미션 존재 안함",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    @Parameters({
            @Parameter(name = "mainMissionId", description = "조회할 메인미션 id 입니다"),
            @Parameter(name = "day", description = "조회할 메인미션 인증사진 날짜입니다 미션 시작일 부터 1일차~ "),
    })
    @GetMapping("/main-mission/{mainMissionId}")
    public BaseResponse<GetMainMissionRes> getMainMission(@PathVariable("mainMissionId") Long mainMissionId,
                                                          @RequestParam("day") int day) throws BaseException{
        try {
            Long userId = (long) jwtService.getUserIdx();
            GetMainMissionRes getMainMissionRes = mainMissionService.getMainMission(mainMissionId, day, userId);

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
    @Tag(name = "mainMissionImageLike")
    @Operation(summary = "메인 미션 인증 사진 좋아요 API")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "JWT4001", description = "JWT 토큰을 주세요!",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "JWT4002", description = "JWT 토큰 만료",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "MISSION4002", description = "메인미션 인증 사진 존재 안함",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "MISSION4008", description = "이미 좋아요 되어 있음",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "MISSION4009", description = "아직 좋아요 하지 않음",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    @Parameters({
            @Parameter(name = "mainMissionProofId", description = "좋아요 처리 할 메인미션의 인증사진 id 입니다"),
    })
    @PostMapping("/main-mission/proof/{mainMissionProofId}/like")
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
    @Tag(name = "mainMissionImageUnLike")
    @Operation(summary = "메인 미션 인증 사진 좋아요 취소 API")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "JWT4001", description = "JWT 토큰을 주세요!",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "JWT4002", description = "JWT 토큰 만료",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "MISSION4002", description = "메인미션 인증 사진 존재 안함",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "MISSION4009", description = "아직 좋아요 하지 않음",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    @Parameters({
            @Parameter(name = "mainMissionProofId", description = "좋아요 취소 처리 할 메인미션의 인증사진 id 입니다"),
    })
    @DeleteMapping("/main-mission/proof/{mainMissionProofId}/unlike")
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
    @Tag(name = "mainMissionImageUnLike")
    @Operation(summary = "메인 미션 인증 사진 신고 API")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "JWT4001", description = "JWT 토큰을 주세요!",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "JWT4002", description = "JWT 토큰 만료",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "ARTICLE4001", description = "이미 신고된 사진",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "MISSION4002", description = "존재하지 않는 사진",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    @Parameters({
            @Parameter(name = "mainMissionProofId", description = "신고 처리 할 메인미션의 인증사진 id 입니다"),
    })
    @PostMapping("/main-mission/proof/{mainMissionProofId}/report")
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

    /**
     * 메인 미션 생성 API
     * @param categoryId
     * @param postMainMissionReq
     * @return
     * @throws BaseException
     */
    @Tag(name = "mainMissionImageUnLike")
    @Operation(summary = "메인 미션 생성 API")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "JWT4001", description = "JWT 토큰을 주세요!",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "JWT4002", description = "JWT 토큰 만료",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "JWT4003", description = "묘집사 자격을 확인해주세요",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "MISSION4007", description = "아직 이전미션이 끝나지 않았습니다",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    @Parameters({
            @Parameter(name = "categoryId", description = "미션생성할 카테고리 id 입니다 해당 카테고리의 이전 미션은 비활성화 처리됩니다."),
    })
    @PostMapping("/host/main-mission/{categoryId}")
    public BaseResponse createMainMission(@PathVariable("categoryId") Long categoryId, @RequestBody PostMainMissionReq postMainMissionReq) throws BaseException {
        System.out.println("jwt" + jwtService.createJwt(1));
        try {
            Long userId = (long) jwtService.getUserIdx();
            mainMissionService.createMainMission(userId, categoryId, postMainMissionReq);
            return new BaseResponse<>(categoryId + "번 카테고리 메인미션 생성완료되었습니다");
        }catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 메인 미션 인증 사진 업로드
     * @param categoryId
     * @return
     * @throws BaseException
     */
    @Tag(name = "mainMissionImageUnLike")
    @Operation(summary = "메인 미션 인증사진 업로드 API")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "JWT4001", description = "JWT 토큰을 주세요!",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "JWT4002", description = "JWT 토큰 만료",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "MISSION4003", description = "오늘 이미 인증을 완료했습니다.",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    @Parameters({
            @Parameter(name = "categoryId", description = "인증 사진을 업로드할 메인 미션의 카테고리 id"),
    })
    @PostMapping("/main-mission/upload/{categoryId}")
    public BaseResponse uploadProofImage(@RequestPart(value = "multipartFile") MultipartFile multipartFile, @PathVariable("categoryId") Long categoryId)throws BaseException{
        try{
            Long userId = (long) jwtService.getUserIdx();
            mainMissionService.uploadProofImage(multipartFile, categoryId, userId);
            return new BaseResponse<>("인증 사진 업로드 완료");

        } catch (BaseException | IOException exception){
            return new BaseResponse<>(FAILED_TO_UPLOAD_PROOF_IMAGE);
        }
    }

    /**
     * 메인 미션 관리 화면 조회
     * @param categoryId
     * @return
     */
    @Tag(name = "mainMissionImageUnLike")
    @Operation(summary = "메인 미션 관리 화면 조회 API")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "JWT4001", description = "JWT 토큰을 주세요!",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "JWT4002", description = "JWT 토큰 만료",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "JWT4003", description = "묘집사 권한이 없는 유저입니다",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    @Parameters({
            @Parameter(name = "categoryId", description = "관리할 카테고리 ID"),
    })
<<<<<<< HEAD:src/main/java/rabbit/umc/com/demo/mainmission/controller/MainMissionController.java
    @GetMapping("/host/main-mission/{categoryId}")
    public BaseResponse<List<MainMissionViewRes>> getMainMissionView(@PathVariable Long categoryId){
=======
    @GetMapping("/host/main-mission/{mainMissionId}")
    public BaseResponse<MainMissionViewRes> getMainMissionView(@PathVariable("mainMissionId") Long mainMissionId){
>>>>>>> 209f690d47c93c8036a607122b103c4511f1f436:src/main/java/rabbit/umc/com/demo/mainmission/MainMissionController.java
        try {
            Long userId = (long) jwtService.getUserIdx();
            List<MainMissionViewRes> mainMissionViewRes = mainMissionService.getMainMissionView(categoryId, userId);
            return new BaseResponse<>(mainMissionViewRes);
        }catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

}
