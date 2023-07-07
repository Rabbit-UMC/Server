package rabbit.umc.com.demo.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rabbit.umc.com.config.BaseException;
import rabbit.umc.com.config.BaseResponse;
import rabbit.umc.com.demo.user.Domain.User;
import rabbit.umc.com.demo.user.Dto.UserEmailNicknameReqDto;
import rabbit.umc.com.demo.user.jwt.JwtProperties;
import rabbit.umc.com.utils.JwtService;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/app/users")
public class UserController {
    private final UserService userService;
    private final KakaoService kakaoService;
    private final EmailService emailService;
    private final JwtService jwtService;

    //카카오 로그인
    @GetMapping("/kakao-login")
    public BaseResponse<String> kakaoLogin(@RequestParam String code, HttpServletResponse response) throws JsonProcessingException {
        System.out.println("kakao code: "+ code);
        User user = kakaoService.kakaoLogin(code, response);

        //jwt 토큰 생성
        String jwtToken = jwtService.createJwt(Math.toIntExact(user.getId()));
        HttpHeaders headers = new HttpHeaders();
        headers.add(JwtProperties.HEADER_STRING, JwtProperties.TOKEN_PREFIX + jwtToken);

        //user_id 돌려주기
        return new BaseResponse<>("user_id: "+user.getId());
    }

    //이메일, 닉네임 수집
    @PostMapping("/sign-up")
    public BaseResponse<UserEmailNicknameReqDto> getEmailandNickname(@RequestBody UserEmailNicknameReqDto userEmailNicknameReqDto) throws BaseException {
        userService.getEmailandNickname(userEmailNicknameReqDto);
        return new BaseResponse<>(userEmailNicknameReqDto);
    }

    //이메일 인증 메일 발송
    @PostMapping("/emailConfirm")
    public BaseResponse<String> emailConfirm(@RequestParam String email) throws Exception {

        String confirm = emailService.sendSimpleMessage(email);

        return new BaseResponse<>(confirm);
    }
}
