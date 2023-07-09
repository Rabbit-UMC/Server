package rabbit.umc.com.demo.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import rabbit.umc.com.config.BaseException;
import rabbit.umc.com.config.BaseResponse;
import rabbit.umc.com.demo.article.dto.ArticleListRes;
import rabbit.umc.com.demo.user.Domain.User;
import rabbit.umc.com.demo.user.Dto.*;
import rabbit.umc.com.demo.user.jwt.JwtProperties;
import rabbit.umc.com.utils.JwtService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
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
        System.out.println("kakao code: "+ code);
        //api
        //엑세스 토큰 받기
        String accessToken = kakaoService.getAccessToken(code);

        User user = kakaoService.kakaoLogin(accessToken);

        if (code != null) {
            String authorize_code = code;
        }

        //jwt 토큰 생성(로그인 처리하기?)

        String jwtToken = jwtService.createJwt(Math.toIntExact(user.getId())/**, accessToken**/);



        HttpHeaders headers = new HttpHeaders();
        headers.add(JwtProperties.HEADER_STRING, JwtProperties.TOKEN_PREFIX + jwtToken);

        UserLoginResDto userLoginResDto = new UserLoginResDto(user.getId(), jwtToken);

        //UserLoginResDto 돌려주기
        return new BaseResponse<>(userLoginResDto);
    }

    /**
     * 이메일, 닉네임 수집
     * @param userEmailNicknameReqDto
     * @return
     * @throws BaseException
     */
    @PostMapping("/sign-up")
    public BaseResponse<UserEmailNicknameDto> getEmailandNickname(@RequestBody UserEmailNicknameDto userEmailNicknameReqDto) throws BaseException {
        userService.isEmailVerified(userEmailNicknameReqDto);
        userService.getEmailandNickname(userEmailNicknameReqDto);
        return new BaseResponse<>(userEmailNicknameReqDto);
    }

    //이메일 인증 메일 발송

    /**
     * 이메일 인증 메일 발송
     * @param email
     * @return
     * @throws Exception
     */
    @PostMapping("/emailConfirm")
    public BaseResponse<String> emailConfirm(@RequestParam String email) throws Exception {

        String authenticationCode = emailService.sendSimpleMessage(email);

        return new BaseResponse<>(authenticationCode);
    }

    /**
     * 이메일 인증 코드 일치
     * @param emailAuthenticationDto
     * @return
     * @throws BaseException
     */
    @PostMapping("/email-check")
    public BaseResponse<String> emailCheck(@RequestBody EmailAuthenticationDto emailAuthenticationDto) throws BaseException{
        emailService.emailCheck(emailAuthenticationDto);
        return new BaseResponse<>("");
    }

    /**
     * 프로필 이미지 수정
     * 수정시 updatedat 수정 기능 추가하기
     * @param userId
     * @param userUpdateProfileImageReqDto
     * @return
     */
    @PatchMapping("/profileImage/{userId}")
    public BaseResponse<Long> updateProfileImage(@PathVariable Long userId,
                                                 @RequestBody UserUpdateProfileImageReqDto userUpdateProfileImageReqDto) throws BaseException {
        userService.updateProfileImage(userId, userUpdateProfileImageReqDto.getUserProfileImage());
        return new BaseResponse<>(userId);
    }

    /**
     * 닉네임 수정
     * 수정시 updatedAt 수정 기능 추가하기
     * @param userId
     * @param userUpdateNicknameReqDto
     * @return
     * @throws BaseException
     */
    @PatchMapping("/nickname/{userId}")
    public BaseResponse<Long> updateNickname(@PathVariable Long userId,
                                         @RequestBody UserUpdateNicknameReqDto userUpdateNicknameReqDto) throws BaseException{
        userService.updateNickname(userId, userUpdateNicknameReqDto.getUserName());
        return new BaseResponse<>(userId);
    }

    /**
     * 유저 프로필 조회
     * @param userId
     * @return
     * @throws BaseException
     */
    @GetMapping("/profile/{userId}")
    public BaseResponse<UserGetProfileResDto> getProfile(@PathVariable Long userId) throws BaseException {
        UserGetProfileResDto userGetProfileResDto = userService.getProfile(userId);
        return new BaseResponse(userGetProfileResDto);
    }

    //유저 작성 글 전체 조회(article에 있는 article id 이용)

    /**
     * 유저가 작성한 글 전체 조회
     * @param page
     * @param userId
     * @return
     * @throws BaseException
     */
    @GetMapping("/articleList")
    public BaseResponse<List<ArticleListRes>> getArticles(@RequestParam(defaultValue = "0", name = "page") int page, @RequestParam Long userId) throws BaseException{
        List<ArticleListRes> articleListRes = userService.getArticles(page, userId);

        return new BaseResponse<>(articleListRes);
    }

    /**
     * 유저가 댓글을 작성한 글 리스트 조회
     * 댓글 2개여도 글 1개면 하나만 나오게???
     * 글 정렬 기준: 최근에 댓글 단 순서 or 글이 써진 순서
     * @param page
     * @param userId
     * @return
     * @throws BaseException
     */
    @GetMapping("/commented-articles")
    public BaseResponse<List<ArticleListRes>> getCommentedArticles(@RequestParam(defaultValue = "0", name = "page") int page, @RequestParam Long userId) throws BaseException{
        List<ArticleListRes> articleListRes = userService.getCommentedArticles(page, userId);

        return new BaseResponse<>(articleListRes);
    }

    //유저 랭킹 조회(각 게시판 별 main mission user에 있는 score 이용)
    @GetMapping("/rank")
    public BaseResponse<Long> getRank(@RequestParam Long userId, @RequestParam Long categoryId){
        long rank = userService.getRank(userId, categoryId);
        return new BaseResponse<>(rank);
    }









    //로그아웃
//    @RequestMapping(value="/kakao-logout")
//    public String logout(HttpSession session) throws IOException {
//        kakaoService.logout((String)session.getAttribute("access_token"));
//        session.invalidate();
//        return "redirect:/";
//    }

//    @GetMapping("/logout")
//    private ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        Optional<Cookie> refreshTokenCookie = Arrays.stream(request.getCookies())
//                .filter(cookie -> cookie.getName().equals("refreshToken")).findFirst();
//
//        if (refreshTokenCookie.isPresent()) {
//            refreshTokenCookie.get().setMaxAge(0);
//            response.addCookie(refreshTokenCookie.get());
//        } // refreshTokenCookie 삭제. HttpOnly여서 서버에서 삭제
//
//        String accessToken = request.getHeader(JwtTokenConstants.HEADER_AUTHORIZATION).replace(JwtTokenConstants.TOKEN_PREFIX, "");
//
//        Jwt jwt = tokenService.getAccessTokenInfo(accessToken);
//        Long loggedoutId = kakaoLogout(jwt.getSocialAccessToken()); // 카카오 로그아웃
//
//        if (loggedoutId == null) {
//            throw new RuntimeException("logout failed");
//        }
//
//        return ResponseEntity.noContent().build();
//    }

    //엑세스 토큰 갱신
//    @GetMapping("/api/jwt/refresh")
//    public ResponseEntity refreshAccessToken(HttpServletRequest request, HttpServletResponse response) {
//        RefreshTokenResponse refreshTokenResponse = null;
//
//        Optional<Cookie> refreshToken = Arrays.stream(request.getCookies()).filter(
//                cookie -> cookie.getName().equals("refreshToken")
//        ).findFirst();
//
//        if (refreshToken.isPresent()) {
//            Jwt jwt = tokenService.refreshAccessToken(refreshToken.get().getValue());
//            refreshTokenResponse = RefreshTokenResponse.builder().accessToken(jwt.getAccessToken())
//                    .accessTokenExp(jwt.getAccessTokenExp()).build();
//        } else {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//        }
//        return ResponseEntity.status(HttpStatus.CREATED).body(refreshTokenResponse);
//    }


    //연결 끊기
//    @RequestMapping(value="/kakao-unlink")
//    public String unlink(HttpSession session) {
//        kakaoService.unlink((String)session.getAttribute("access_token"));
//        session.invalidate();
//        return "redirect:/";
//    }
}
