package rabbit.umc.com.demo.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rabbit.umc.com.config.BaseResponse;
import rabbit.umc.com.demo.user.Domain.User;
import rabbit.umc.com.demo.user.jwt.JwtProperties;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/app/users")
//@RequestMapping("/oauth")
public class KakaoController {
    @Autowired
    private final KakaoService kakaoService;

    //카카오 로그인
    @GetMapping("/kakao-login")
    public BaseResponse<String> kakaoLogin(@RequestParam String code, HttpServletResponse response) throws JsonProcessingException {
        System.out.println("kakao code: "+ code);
        String jwtToken = kakaoService.kakaoLogin(code, response);
        HttpHeaders headers = new HttpHeaders();
        headers.add(JwtProperties.HEADER_STRING, JwtProperties.TOKEN_PREFIX + jwtToken);

        return new BaseResponse<>("");
    }
}
