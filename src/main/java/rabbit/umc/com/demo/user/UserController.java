package rabbit.umc.com.demo.user;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import rabbit.umc.com.config.BaseException;
import rabbit.umc.com.config.BaseResponse;
import rabbit.umc.com.demo.Status;
import rabbit.umc.com.demo.mission.dto.MissionHistoryRes;
import rabbit.umc.com.demo.user.Domain.User;
import rabbit.umc.com.demo.user.Dto.*;
import rabbit.umc.com.utils.JwtService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import static rabbit.umc.com.config.BaseResponseStatus.*;


@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/app/users")
public class UserController {
    private final UserService userService;
    private final KakaoService kakaoService;
    private final JwtService jwtService;


    /**
     * 카카오 로그인 api
     // * @param accessToken
     * @param response
     * @return
     * @throws IOException
     * @throws BaseException
     */
    @GetMapping("/kakao-login")
    public BaseResponse<UserLoginResDto> kakaoLogin(@RequestHeader("Authorization") String accessToken, HttpServletResponse response) throws IOException, BaseException {
        try {
            if (accessToken == null) {
                throw new BaseException(EMPTY_KAKAO_ACCESS);
            }

            KakaoDto kakaoDto = kakaoService.findProfile(accessToken);
            User user = kakaoService.saveUser(kakaoDto);

            //jwt 토큰 생성(로그인 처리)
            String jwtAccessToken = jwtService.createJwt(Math.toIntExact(user.getId()));
            String jwtRefreshToken = jwtService.createRefreshToken();
            System.out.println(jwtAccessToken);
            System.out.println(jwtRefreshToken);
            userService.saveRefreshToken(user.getId(), jwtRefreshToken);
            UserLoginResDto userLoginResDto = new UserLoginResDto(user.getId(), jwtAccessToken, jwtRefreshToken);

            return new BaseResponse<>(userLoginResDto);
        }
        catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

//    @GetMapping("/kakao-login")
//    public BaseResponse<UserLoginResDto> kakaoLogin(@RequestParam String code, HttpServletResponse response) throws IOException, BaseException {
//        try {
//            String accessToken = kakaoService.getAccessToken(code);
//
//            KakaoDto kakaoDto = kakaoService.findProfile(accessToken);
//            User user = kakaoService.saveUser(kakaoDto);
//
//            //jwt 토큰 생성(로그인 처리)
//            String jwtAccessToken = jwtService.createJwt(Math.toIntExact(user.getId()));
//            String jwtRefreshToken = jwtService.createRefreshToken();
//            System.out.println(jwtAccessToken);
//            System.out.println(jwtRefreshToken);
//            userService.saveRefreshToken(user.getId(), jwtRefreshToken);
//            UserLoginResDto userLoginResDto = new UserLoginResDto(user.getId(), jwtAccessToken, jwtRefreshToken);
//
//            return new BaseResponse<>(userLoginResDto);
//        }
//        catch (BaseException exception) {
//            return new BaseResponse<>(exception.getStatus());
//        }
//    }

    /**
     * 회원 탈퇴(카카오 연결 끊기)
     * @param response
     * @return
     * @throws BaseException
     * @throws IOException
     */
    @GetMapping("/kakao-unlink")
    public BaseResponse<Long> kakaoUnlink(HttpServletResponse response) throws BaseException, IOException {
        try {
            //jwt 토큰으로 로그아웃할 유저 아이디 받아오기
            int userId = jwtService.getUserIdx();

            //유저 아이디로 카카오 아이디 받아오기
            User user = userService.findUser(Long.valueOf(userId));
            userService.delRefreshToken(user);
            Long kakaoId = user.getKakaoId();
            Long logout_kakaoId = kakaoService.unlink(kakaoId);
            user.setStatus(Status.INACTIVE);

            log.info("회원 탈퇴가 완료되었습니다.");
            return new BaseResponse<>(logout_kakaoId);
        }
        catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 회원가입시, 닉네임 수집
     * @return
     * @throws BaseException
     */
    @PostMapping("/sign-up")
    public BaseResponse<UserNicknameResDto> getNickname(@RequestBody UserNicknameReqDto userNicknameReqDto) throws BaseException {
        try{
            Long userId = (long) jwtService.getUserIdx();
            userService.getNickname(userId, userNicknameReqDto.getUserName());
            UserNicknameResDto userNicknameResDto = new UserNicknameResDto(userId, userNicknameReqDto.getUserName());
            return new BaseResponse<>(userNicknameResDto);
        }
        catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }

    }

    /**
     * 닉네임, 프로필 이미지 수정
     * @param userProfileImage
     * @param userName
     * @return
     */
    @PatchMapping("/profile")
    public BaseResponse<Long> updateProfile(@RequestParam String userProfileImage, @RequestParam String userName){
        try {
            Long userId = (long) jwtService.getUserIdx();
            User user = userService.findUser(userId);
            System.out.println("닉네임을 "+user.getUserName()+"에서 "+userName+"으로 변경합니다. 회원번호: "+userId);
            System.out.println("프로필 이미지를 "+user.getUserProfileImage()+"에서 "+userProfileImage+"으로 변경합니다. 회원번호: "+userId);

            userService.updateProfile(userId, userName, userProfileImage);
            return new BaseResponse<>(userId);
        }
        catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 닉네임 중복확인
     * @param userName
     * @return
     * @throws BaseException
     */
    @GetMapping("/checkDuplication")
    public BaseResponse<Boolean> updateNickname(@RequestParam String userName) throws BaseException{
        try {
            Long jwtUserId = (long) jwtService.getUserIdx();
            return new BaseResponse<>(userService.isExistSameNickname(userName, jwtUserId));
        }
        catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 유저 프로필 조회
     * @return
     * @throws BaseException
     */
    @GetMapping("/profile")
    public BaseResponse<UserGetProfileResDto> getProfile() throws BaseException {
        try {
            Long jwtUserId = (long) jwtService.getUserIdx();
            System.out.println("프로필을 조회합니다. 회원번호: "+jwtUserId);
            UserGetProfileResDto userGetProfileResDto = userService.getProfile(jwtUserId);
            return new BaseResponse(userGetProfileResDto);
        }
        catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 유저가 작성한 글 전체 조회
     * @param page
     * @return
     * @throws BaseException
     */
    @GetMapping("/articleList")
    public BaseResponse<List<UserArticleListResDto>> getArticles(@RequestParam(defaultValue = "0", name = "page") int page) throws BaseException {
        try {
            Long jwtUserId = (long) jwtService.getUserIdx();
            System.out.println("유저가 작성한 글을 조회합니다. 회원번호: "+jwtUserId);
            List<UserArticleListResDto> userArticleListResDtos = userService.getArticles(page, jwtUserId);
            return new BaseResponse<>(userArticleListResDtos);
        }
        catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 유저가 댓글단 글 전체 조회
     * @param page
     * @return
     * @throws BaseException
     */
    @GetMapping("/commented-articles")
    public BaseResponse<List<UserArticleListResDto>> getCommentedArticles(@RequestParam(defaultValue = "0", name = "page") int page) throws BaseException
    {
        try {
            Long jwtUserId = (long) jwtService.getUserIdx();
            System.out.println("유저가 댓글단 글을 조회합니다. 회원번호: "+jwtUserId);
            List<UserArticleListResDto> userArticleListResDtos = userService.getCommentedArticles(page, jwtUserId);
            return new BaseResponse<>(userArticleListResDtos);
        }
        catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * access token 재발급
     * @param accessToken
     * @param refreshToken
     * @return
     * @throws BaseException
     */
    @GetMapping("/reissue")
    public BaseResponse<ReissueTokenDto> reissueToken(@RequestHeader("X-ACCESS-TOKEN") String accessToken, @RequestHeader("X-REFRESH-TOKEN") String refreshToken) throws BaseException{
        try{
            ReissueTokenDto reissueTokenDto = null;
            if(jwtService.getExpirationDate(accessToken).before(new Date())){
                //만료된 accessToken 이용해서 user id 알아내기
                Long userId = jwtService.getUserIdFromToken(accessToken);
                boolean canReissue = userService.isReissueAllowed(userId, refreshToken);

                if(canReissue){
                    String jwtToken = jwtService.createJwt(Math.toIntExact(userId));
                    reissueTokenDto = new ReissueTokenDto(userId, jwtToken, refreshToken);
                    System.out.println(jwtToken);
                } else{
                    User user = userService.findUser(Long.valueOf(userId));
                    userService.delRefreshToken(user);
                    throw new BaseException(INVALID_JWT_REFRESH);
                }
            }else{
                log.info("access token의 유효기간이 남아있어 재발급이 불가합니다.");
                throw new BaseException(UNEXPIRED_JWT_ACCESS);
            }
            return new BaseResponse<>(reissueTokenDto);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 성공 히스토리
     */
    @GetMapping("/success")
    public BaseResponse<UserMissionHistoryDto> getSuccessMissions(){
        try {
            Long userId = (long) jwtService.getUserIdx();
            UserMissionHistoryDto result = userService.getSuccessMissions(userId);
            return new BaseResponse<>(result);
        } catch (BaseException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * 실패 히스토리
     */
    @GetMapping("/failure")
    public BaseResponse<UserMissionHistoryDto> getFailureMissions(){
        try {
            Long userId = (long) jwtService.getUserIdx();
            UserMissionHistoryDto result = userService.getFailureMissions(userId);
            return new BaseResponse<>(result);
        } catch (BaseException e) {
            return new BaseResponse(e.getMessage());
        }
    }

}