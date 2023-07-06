package rabbit.umc.com.demo.user;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rabbit.umc.com.config.BaseException;
import rabbit.umc.com.config.BaseResponse;
import rabbit.umc.com.demo.user.Dto.UserEmailNicknameReqDto;

@RestController
@RequiredArgsConstructor
@RequestMapping("/app/users")
public class UserController {
    @Autowired
    UserService userService;

    @PostMapping("/sign-up")
    public BaseResponse<String> getEmailandNickname(@RequestBody UserEmailNicknameReqDto userEmailNicknameReqDto) throws BaseException {
        userService.getEmailandNickname(userEmailNicknameReqDto);
        return new BaseResponse<>("이메일 인증 코드를 눌러주세요");
    }
}
