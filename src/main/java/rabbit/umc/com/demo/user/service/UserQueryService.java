package rabbit.umc.com.demo.user.service;

import static rabbit.umc.com.demo.user.Domain.UserPermission.HOST;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rabbit.umc.com.config.apiPayload.BaseException;
import rabbit.umc.com.config.apiPayload.BaseResponseStatus;
import rabbit.umc.com.demo.user.Domain.User;
import rabbit.umc.com.demo.user.repository.UserRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserQueryService {
    private final UserRepository userRepository;

    public User getUser(Long userId){
        return userRepository.getReferenceById(userId);
    }

    public boolean isHostUser(Long userId){
        User user = userRepository.getReferenceById(userId);
        return user.getUserPermission() == HOST;
    }

    public User getUserByUserId(Long userId) throws BaseException {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BaseException( BaseResponseStatus.USER_NOT_FOUND));
    }
}
