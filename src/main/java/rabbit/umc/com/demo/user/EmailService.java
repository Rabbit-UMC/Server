package rabbit.umc.com.demo.user;

import rabbit.umc.com.config.BaseException;
import rabbit.umc.com.demo.user.Dto.EmailAuthenticationDto;

public interface EmailService {
    String sendSimpleMessage(String to)throws Exception;
    void emailCheck(EmailAuthenticationDto emailAuthenticationDto)throws BaseException;
}
