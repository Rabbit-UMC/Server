package rabbit.umc.com.demo.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import rabbit.umc.com.config.BaseException;
import rabbit.umc.com.config.BaseResponse;
import rabbit.umc.com.demo.Status;
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
    private final EmailService emailService;
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
    public BaseResponse<UserLoginResDto> kakaoLogin(@RequestHeader("Authorization") String accessToken, /*@RequestParam String code, */HttpServletResponse response) throws IOException, BaseException {
        try {
            if (accessToken == null) {
                throw new BaseException(EMPTY_KAKAO_ACCESS);
            }
            //String accessToken = kakaoService.getAccessToken(code);

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


    /**
     * 카카오 로그아웃
     * @return
     * @throws BaseException
     * @throws IOException
     */
    @GetMapping("/kakao-logout")
    public BaseResponse<Long> kakaoLogout(HttpServletResponse response) throws BaseException, IOException {
        try {
            int userId = jwtService.getUserIdx();
            System.out.println(userId);

            User user = userService.findUser(Long.valueOf(userId));
            userService.delRefreshToken(user);
            Long kakaoId = user.getKakaoId();
            Long logout_kakaoId = kakaoService.logout(kakaoId);

            log.info("로그아웃이 완료되었습니다.");
            return new BaseResponse<>(logout_kakaoId);
        }
        catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

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
     * 이메일, 닉네임 수집
     * @param userEmailNicknameReqDto
     * @return
     * @throws BaseException
     */
    @PostMapping("/sign-up")
    public BaseResponse<UserEmailNicknameResDto> getEmailandNickname(@RequestBody UserEmailNicknameReqDto userEmailNicknameReqDto) throws BaseException {
        try{
        Long userId = (long) jwtService.getUserIdx();
        userService.isEmailVerified(userEmailNicknameReqDto);
        userService.getEmailandNickname(userId, userEmailNicknameReqDto);
        UserEmailNicknameResDto userEmailNicknameResDto = new UserEmailNicknameResDto(userId, userEmailNicknameReqDto.getUserEmail(), userEmailNicknameReqDto.getUserName());
        return new BaseResponse<>(userEmailNicknameResDto);
        }
        catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }

    }

    /**
     * 이메일 인증 메일 발송
     * @return
     * @throws Exception
     */
    @PostMapping("/emailConfirm")
    public BaseResponse<String> emailConfirm() throws Exception {
        try {
            Long userId = (long) jwtService.getUserIdx();
            User user = userService.findUser(userId);
            String email = user.getUserEmail();
            String authenticationCode = emailService.sendSimpleMessage(email);

            return new BaseResponse<>(authenticationCode);
        }
        catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 이메일 인증 코드 일치
     * @param emailAuthenticationDto
     * @return
     * @throws BaseException
     */
    @PostMapping("/email-check")
    public BaseResponse<String> emailCheck(@RequestBody EmailAuthenticationDto emailAuthenticationDto) throws BaseException{
        try {
            Long userId = (long) jwtService.getUserIdx();
            if (userId != emailAuthenticationDto.getUserId()) {
                throw new BaseException(INVALID_USER_JWT);
            }
            emailService.emailCheck(emailAuthenticationDto);
            return new BaseResponse<>("인증 성공!");
        }
        catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 프로필 이미지 수정
     * @param userProfileImage
     * @return
     * @throws BaseException
     */
    @PatchMapping("/profileImage")
    public BaseResponse<Long> updateProfileImage(@RequestParam String userProfileImage) throws BaseException {
        try {
            Long userId = (long) jwtService.getUserIdx();
            userService.updateProfileImage(userId, userProfileImage);
            return new BaseResponse<>(userId);
        }
        catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }


    /**
     * 닉네임 수정
     * @param userName
     * @return
     * @throws BaseException
     */
    @PatchMapping("/nickname")
    public BaseResponse<Long> updateNickname(@RequestParam String userName) throws BaseException{
        try {
            Long userId = (long) jwtService.getUserIdx();
            userService.updateNickname(userId, userName);
            return new BaseResponse<>(userId);
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
    public BaseResponse<UserGetProfileResDto> getProfile(/*@PathVariable Long userId*/) throws BaseException {
        try {
            Long jwtUserId = (long) jwtService.getUserIdx();
//            if (jwtUserId != userId) {
//                throw new BaseException(INVALID_USER_JWT);
//            }
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
    public BaseResponse<List<UserArticleListResDto>> getArticles(@RequestParam(defaultValue = "0", name = "page") int page/*,
                                                                 @RequestParam Long userId*/) throws BaseException {
        try {
            Long jwtUserId = (long) jwtService.getUserIdx();
//            if (jwtUserId != userId) {
//                throw new BaseException(INVALID_USER_JWT);
//            }
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
    public BaseResponse<List<UserArticleListResDto>> getCommentedArticles(@RequestParam(defaultValue = "0", name = "page") int page/*,
                                                                          @RequestParam Long userId*/) throws BaseException
        {
            try {
                Long jwtUserId = (long) jwtService.getUserIdx();
//                if (jwtUserId != userId) {
//                    throw new BaseException(INVALID_USER_JWT);
//                }
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
            //access token 만료된거 맞는지 확인
            if(jwtService.getExpirationDate(accessToken).before(new Date())){
                //만료된 accessToken 이용해서 user id 알아내기
                Long userId = jwtService.getUserIdFromToken(accessToken);
                boolean canReissue = userService.isReissueAllowed(userId, refreshToken);

                if(canReissue){
                    String jwtToken = jwtService.createJwt(Math.toIntExact(userId));
                    reissueTokenDto = new ReissueTokenDto(userId, jwtToken, refreshToken);
                    System.out.println(jwtToken);
                } else{
                    //로그아웃
                    User user = userService.findUser(Long.valueOf(userId));
                    userService.delRefreshToken(user);
                    Long kakaoId = user.getKakaoId();
                    Long logout_kakaoId = kakaoService.logout(kakaoId);
                    log.info("로그아웃되었습니다. kakao id: "+logout_kakaoId);
                }
            }else{
                log.info("access token의 유효기간이 남아있어 재발급이 불가합니다.");
                throw new BaseException(UNEXPIRED_JWT_ACCESS);
            }
            return new BaseResponse<>(reissueTokenDto);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
