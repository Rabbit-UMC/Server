package rabbit.umc.com.demo.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import rabbit.umc.com.config.BaseException;
import rabbit.umc.com.config.BaseResponse;
import rabbit.umc.com.demo.Status;
import rabbit.umc.com.demo.user.Domain.User;
import rabbit.umc.com.demo.user.Dto.*;
import rabbit.umc.com.demo.user.property.JwtAndKakaoProperties;
import rabbit.umc.com.utils.JwtService;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static rabbit.umc.com.config.BaseResponseStatus.INVALID_USER_JWT;
import static rabbit.umc.com.config.BaseResponseStatus.RESPONSE_ERROR;


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
     * @param code
     * @param response
     * @return
     * @throws IOException
     * @throws BaseException
     */
    @GetMapping("/kakao-login")
    public BaseResponse<UserLoginResDto> kakaoLogin(@RequestParam String code, HttpServletResponse response) throws IOException, BaseException {
        //System.out.println("kakao code: "+ code);
        //api
        //엑세스 토큰 받기
        String accessToken = kakaoService.getAccessToken(code);

        User user = kakaoService.kakaoLogin(accessToken);

        //jwt 토큰 생성(로그인 처리)
        String jwtToken = jwtService.createJwt(Math.toIntExact(user.getId()));
        System.out.println(jwtToken);

        HttpHeaders headers = new HttpHeaders();
        headers.add(JwtAndKakaoProperties.HEADER_STRING, JwtAndKakaoProperties.TOKEN_PREFIX + jwtToken);
        Cookie cookie = new Cookie("jwtToken",jwtToken);

        response.addCookie(cookie);

        UserLoginResDto userLoginResDto = new UserLoginResDto(user.getId(), jwtToken);

        return new BaseResponse<>(userLoginResDto);
    }


    /**
     * 카카오 로그아웃
     * @return
     * @throws BaseException
     * @throws IOException
     */
    @GetMapping("/kakao-logout")
    public BaseResponse<Long> kakaoLogout(@CookieValue(value = "jwtToken", required = false) String jwtToken, HttpServletResponse response) throws BaseException, IOException {
        //쿠키가 없을 때
        if(jwtToken == null){
            log.info("쿠키가 존재하지 않습니다.");
            throw new BaseException(RESPONSE_ERROR);
        }

        //jwt 토큰으로 로그아웃할 유저 아이디 받아오기
        int userId = jwtService.getUserIdByCookie(jwtToken);

        //유저 아이디로 카카오 아이디 받아오기
        User user = userService.findUser(Long.valueOf(userId));
        Long kakaoId = user.getKakaoId();
        Long logout_kakaoId = kakaoService.logout(kakaoId);

        //쿠키 삭제
        Cookie cookie = new Cookie("jwtToken",null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        log.info("로그아웃이 완료되었습니다.");
        return new BaseResponse<>(logout_kakaoId);
    }

    /**
     * 회원 탈퇴(카카오 연결 끊기)
     * @param jwtToken
     * @param response
     * @return
     * @throws BaseException
     * @throws IOException
     */
    @GetMapping("/kakao-unlink")
    public BaseResponse<Long> kakaoUnlink(@CookieValue(value = "jwtToken", required = false) String jwtToken, HttpServletResponse response) throws BaseException, IOException {
        //쿠키가 없을 때
        if(jwtToken == null){
            log.info("쿠키가 존재하지 않습니다.");
            throw new BaseException(RESPONSE_ERROR);
        }

        //jwt 토큰으로 로그아웃할 유저 아이디 받아오기
        int userId = jwtService.getUserIdByCookie(jwtToken);

        //유저 아이디로 카카오 아이디 받아오기
        User user = userService.findUser(Long.valueOf(userId));
        Long kakaoId = user.getKakaoId();
        Long logout_kakaoId = kakaoService.unlink(kakaoId);

        //status inactive로 바꾸기
        user.setStatus(Status.INACTIVE);

        //쿠키 삭제
        Cookie cookie = new Cookie("jwtToken",null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        log.info("회원 탈퇴가 완료되었습니다.");
        return new BaseResponse<>(logout_kakaoId);
    }

    /**
     * 이메일, 닉네임 수집
     * @param userEmailNicknameReqDto
     * @return
     * @throws BaseException
     */
    @PostMapping("/sign-up")
    public BaseResponse<UserEmailNicknameDto> getEmailandNickname(@CookieValue(value = "jwtToken", required = false) String jwtToken,
                                                                  @RequestBody UserEmailNicknameDto userEmailNicknameReqDto) throws BaseException {
        if(jwtToken == null){
            log.info("쿠키가 존재하지 않습니다.");
            throw new BaseException(RESPONSE_ERROR);
        }
        Long userId = (long) jwtService.getUserIdByCookie(jwtToken);
        userService.isEmailVerified(userEmailNicknameReqDto);
        userService.getEmailandNickname(userId, userEmailNicknameReqDto);
        return new BaseResponse<>(userEmailNicknameReqDto);
    }

    /**
     * 이메일 인증 메일 발송
     * @param jwtToken
     * @return
     * @throws Exception
     */
    @PostMapping("/emailConfirm")
    public BaseResponse<String> emailConfirm(/*@RequestParam String email*/@CookieValue(value = "jwtToken", required = false) String jwtToken) throws Exception {
        if(jwtToken == null){
            log.info("쿠키가 존재하지 않습니다.");
            throw new BaseException(RESPONSE_ERROR);
        }
        Long userId = (long) jwtService.getUserIdByCookie(jwtToken);
        User user = userService.findUser(userId);
        String email = user.getUserEmail();
        String authenticationCode = emailService.sendSimpleMessage(email);

        return new BaseResponse<>(authenticationCode);
    }

    /**
     * 이메일 인증 코드 일치
     * @param jwtToken
     * @param emailAuthenticationDto
     * @return
     * @throws BaseException
     */
    @PostMapping("/email-check")
    public BaseResponse<String> emailCheck(@CookieValue(value = "jwtToken", required = false) String jwtToken,
                                           @RequestBody EmailAuthenticationDto emailAuthenticationDto) throws BaseException{
        if(jwtToken == null){
            log.info("쿠키가 존재하지 않습니다.");
            throw new BaseException(RESPONSE_ERROR);
        }
        Long userId = (long) jwtService.getUserIdByCookie(jwtToken);
        if(userId != emailAuthenticationDto.getUserId()){
            throw new BaseException(INVALID_USER_JWT);
        }
        emailService.emailCheck(emailAuthenticationDto);
        return new BaseResponse<>("인증 성공!");
    }

    /**
     * 프로필 이미지 수정
     * @param jwtToken
     * @param userProfileImage
     * @return
     * @throws BaseException
     */
    @PatchMapping("/profileImage")
    public BaseResponse<Long> updateProfileImage(@CookieValue(value = "jwtToken", required = false) String jwtToken,/*, @RequestParam Long userId,*/
                                                 @RequestParam String userProfileImage) throws BaseException {
        if(jwtToken == null){
            log.info("쿠키가 존재하지 않습니다.");
            throw new BaseException(RESPONSE_ERROR);
        }
        Long userId = (long) jwtService.getUserIdByCookie(jwtToken);
        userService.updateProfileImage(userId, userProfileImage);
        return new BaseResponse<>(userId);
    }


    /**
     * 닉네임 수정
     * @param jwtToken
     * @param userName
     * @return
     * @throws BaseException
     */
    @PatchMapping("/nickname")
    public BaseResponse<Long> updateNickname(@CookieValue(value = "jwtToken", required = false) String jwtToken, /*@RequestParam Long userId,*/
                                             @RequestParam String userName) throws BaseException{
        if(jwtToken == null){
            log.info("쿠키가 존재하지 않습니다.");
            throw new BaseException(RESPONSE_ERROR);
        }
        Long userId = (long) jwtService.getUserIdByCookie(jwtToken);
        userService.updateNickname(userId, userName);
        return new BaseResponse<>(userId);
    }

    /**
     * 유저 프로필 조회
     * @param userId
     * @return
     * @throws BaseException
     */
    @GetMapping("/profile/{userId}")
    public BaseResponse<UserGetProfileResDto> getProfile(@CookieValue(value = "jwtToken", required = false) String jwtToken,
                                                         @PathVariable Long userId) throws BaseException {
        if(jwtToken == null){
            log.info("쿠키가 존재하지 않습니다.");
            throw new BaseException(RESPONSE_ERROR);
        }
        Long jwtUserId = (long) jwtService.getUserIdByCookie(jwtToken);
        if(jwtUserId != userId){
            throw new BaseException(INVALID_USER_JWT);
        }
        UserGetProfileResDto userGetProfileResDto = userService.getProfile(jwtUserId);
        return new BaseResponse(userGetProfileResDto);
    }

    /**
     * 유저가 작성한 글 전체 조회
     * @param page
     * @param userId
     * @return
     * @throws BaseException
     */
    @GetMapping("/articleList")
    public BaseResponse<List<UserArticleListResDto>> getArticles(@CookieValue(value = "jwtToken", required = false) String jwtToken,
                                                                 @RequestParam(defaultValue = "0", name = "page") int page,
                                                                 @RequestParam Long userId) throws BaseException{
        if(jwtToken == null){
            log.info("쿠키가 존재하지 않습니다.");
            throw new BaseException(RESPONSE_ERROR);
        }
        Long jwtUserId = (long) jwtService.getUserIdByCookie(jwtToken);
        if(jwtUserId != userId){
            throw new BaseException(INVALID_USER_JWT);
        }
        List<UserArticleListResDto> userArticleListResDtos = userService.getArticles(page, jwtUserId);

        return new BaseResponse<>(userArticleListResDtos);
    }

    /**
     * 유저가 댓글을 작성한 글 리스트 조회
     * @param page
     * @param userId
     * @return
     * @throws BaseException
     */
    @GetMapping("/commented-articles")
    public BaseResponse<List<UserArticleListResDto>> getCommentedArticles(@CookieValue(value = "jwtToken", required = false) String jwtToken,
                                                                          @RequestParam(defaultValue = "0", name = "page") int page,
                                                                          @RequestParam Long userId) throws BaseException{
        if(jwtToken == null){
            log.info("쿠키가 존재하지 않습니다.");
            throw new BaseException(RESPONSE_ERROR);
        }
        Long jwtUserId = (long) jwtService.getUserIdByCookie(jwtToken);
        if(jwtUserId != userId){
            throw new BaseException(INVALID_USER_JWT);
        }
        List<UserArticleListResDto> userArticleListResDtos = userService.getCommentedArticles(page, jwtUserId);

        return new BaseResponse<>(userArticleListResDtos);
    }

//    /**
//     * 유저 랭킹 조회
//     * @param userId
//     * @param categoryId
//     * @return
//     * @throws BaseException
//     */
//    @GetMapping("/rank")
//    public BaseResponse<UserRankResDto> getRank(@RequestParam Long userId, @RequestParam Long categoryId) throws BaseException {
//        long rank = userService.getRank(userId, categoryId);
//        UserRankResDto userRankResDto = new UserRankResDto(categoryId, userId, rank);
//        return new BaseResponse<>(userRankResDto);
//    }
}
