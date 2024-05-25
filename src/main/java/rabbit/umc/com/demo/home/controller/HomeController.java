package rabbit.umc.com.demo.home.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rabbit.umc.com.config.apiPayload.BaseException;
import rabbit.umc.com.config.apiPayload.BaseResponse;
import rabbit.umc.com.demo.community.dto.CommunityHomeRes;
import rabbit.umc.com.demo.community.dto.CommunityHomeResV2;
import rabbit.umc.com.demo.home.facade.HomeFacade;
import rabbit.umc.com.utils.JwtService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/app/home")
public class HomeController {

    private final HomeFacade homeFacade;
    private final JwtService jwtService;

    /**
     * 커뮤니티 홈화면 API
     * @return
     * @throws BaseException
     */
    @Tag(name = "communityHomeV1")
    @Operation(summary = "커뮤니티 홈 화면 조회 API V[1.0]")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공"),
    })
    @GetMapping("/v1")
    public BaseResponse<CommunityHomeRes> communityHome (){
        return new BaseResponse<>(homeFacade.getHomeV1());
    }

    @Tag(name = "communityHomeV2")
    @Operation(summary = "커뮤니티 홈 화면 조회 API V[2.0]")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "JWT4001", description = "JWT 토큰을 주세요!",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "JWT4002", description = "JWT 토큰 만료",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    @GetMapping("/v2")
    public BaseResponse<CommunityHomeResV2> communityHomeV2 (){
        try {
            Long userId = (long) jwtService.getUserIdx();
            return new BaseResponse<>(homeFacade.getHomeV2(userId));
        }catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }
}
