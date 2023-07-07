package rabbit.umc.com.demo.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import rabbit.umc.com.config.BaseException;
import rabbit.umc.com.config.BaseResponseStatus;
import rabbit.umc.com.demo.user.Domain.User;
import rabbit.umc.com.demo.user.Dto.UserEmailNicknameReqDto;

import java.util.Optional;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

    public void getEmailandNickname(UserEmailNicknameReqDto userEmailNicknameReqDto) throws BaseException {
        //해당 유저 아이디 먼저 찾고
        Optional<User> optionalUser = userRepository.findById(userEmailNicknameReqDto.getId());
        User user = optionalUser.orElseThrow(() -> new BaseException(BaseResponseStatus.RESPONSE_ERROR));
        user.setUserName(userEmailNicknameReqDto.getUserName());
        user.setUserEmail(userEmailNicknameReqDto.getUserEmail());
        //해당 유저 엔티티에 나머지값 업데이트
        userRepository.save(user);
    }
}
