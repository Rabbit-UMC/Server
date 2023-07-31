package rabbit.umc.com.demo.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import rabbit.umc.com.config.BaseException;
import rabbit.umc.com.config.BaseResponseStatus;
import rabbit.umc.com.demo.Status;
import rabbit.umc.com.demo.article.domain.Article;
import rabbit.umc.com.demo.user.Domain.User;
import rabbit.umc.com.demo.user.Dto.UserArticleListResDto;
import rabbit.umc.com.demo.user.Dto.UserCommentedArticleListResDto;
import rabbit.umc.com.demo.user.Dto.UserEmailNicknameDto;
import rabbit.umc.com.demo.user.Dto.UserGetProfileResDto;
import rabbit.umc.com.demo.user.property.JwtAndKakaoProperties;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static rabbit.umc.com.config.BaseResponseStatus.*;

@Service
@Slf4j
public class UserService {
    @Autowired
    UserRepository userRepository;

    //유저 아이디로 User 객체 찾기
    public User findUser(Long id) throws BaseException {
        Optional<User> optionalUser = userRepository.findById(id);
        User user;
        if (optionalUser.isPresent()) {
            user = optionalUser.get();
        } else {
            log.info("데이터 베이스에서 찾을 수 없는 user id입니다.");
            throw new BaseException(BaseResponseStatus.USERS_EMPTY_USER_ID);
        }

        if(user.getStatus() == Status.INACTIVE){
            log.info("탈퇴한 회원입니다.");
            throw new BaseException(BaseResponseStatus.INVALID_USER_ID);
        }
        return user;
    }

    //유저 email, nickname 저장

    public void getEmailandNickname(Long userId,UserEmailNicknameDto userEmailNicknameReqDto) throws BaseException {
        User user = findUser(userId);

        if(isExistSameNickname(userEmailNicknameReqDto.getUserName())){
            log.info("중복된 닉네임입니다.");
            throw new BaseException(POST_USERS_EXISTS_NICKNAME);
        }

        user.setUserName(userEmailNicknameReqDto.getUserName());
        user.setUserEmail(userEmailNicknameReqDto.getUserEmail());

        userRepository.save(user);
    }

    public boolean isExistSameNickname(String nickname){
        boolean isExistSameName = userRepository.existsByUserName(nickname);
        return isExistSameName;
    }

    //이메일 형식 검증
    public void isEmailVerified(UserEmailNicknameDto userEmailNicknameReqDto) throws BaseException {
        String email = userEmailNicknameReqDto.getUserEmail();

        // 이메일 형식을 검증하기 위한 정규 표현식
        String emailPattern = "[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}";

        // 정규 표현식과 입력된 이메일을 비교하여 형식을 검증
        Pattern pattern = Pattern.compile(emailPattern);
        Matcher matcher = pattern.matcher(email);

        if (!matcher.matches()) {
            log.info("이메일 형식이 잘못되었습니다.");
            throw new BaseException(POST_USERS_INVALID_EMAIL);
        }
    }

    //프로필 이미지 수정
    @Transactional
    public void updateProfileImage(Long userId, String newProfileImage) throws BaseException {
        //userRepository.updateUserUserProfileImageById(id, newProfileImage);

        User user = findUser(userId);
        user.setUserProfileImage(newProfileImage);
        //userRepository.save(user);
    }

    //닉네임 수정
    @Transactional
    public void updateNickname(Long userId, String newNickname) throws BaseException {
        //userRepository.updateUserUserNameById(id, newNickname);

        User user = findUser(userId);
        if(isExistSameNickname(user.getUserName())){
            log.info("중복된 닉네임입니다.");
            throw new BaseException(POST_USERS_EXISTS_NICKNAME);
        }

        user.setUserProfileImage(newNickname);
        //userRepository.save(user);
    }

    //유저 프로필 조회
    public UserGetProfileResDto getProfile(Long id) throws BaseException {
        User user = findUser(id);
        UserGetProfileResDto userGetProfileResDto = new UserGetProfileResDto(user.getUserEmail(), user.getUserName(), user.getUserProfileImage());
        return userGetProfileResDto;
    }

    public List<UserArticleListResDto> getArticles(int page, Long userId) {

        int pageSize = 20;

        PageRequest pageRequest = PageRequest.of(page, pageSize, Sort.by("createdAt").descending());

        List<Article> articlePage = userRepository.findArticlesByUserIdOrderByCreatedAtDesc(userId, pageRequest);

        List<UserArticleListResDto> userArticleListResDtos = articlePage.stream()
                .map(UserArticleListResDto::toArticleListRes)
                .collect(Collectors.toList());

        return userArticleListResDtos;
    }

    public List<UserArticleListResDto> getCommentedArticles(int page, Long userId) {
        int pageSize = 20;

        PageRequest pageRequest = PageRequest.of(page, pageSize, Sort.by("createdAt").descending());

        List<UserCommentedArticleListResDto> articlePage = userRepository.findCommentedArticlesByUserId(userId, pageRequest);

        List<UserArticleListResDto> userArticleListResDtos = articlePage.stream()
                .map(UserCommentedArticleListResDto::toArticleListRes)
                .collect(Collectors.toList());

        return userArticleListResDtos;
    }

//    public void addAuthorizationHeaderWithJwtToken(String jwtToken) throws BaseException{
//        //쿠키가 없을 때
//        if (jwtToken == null) {
//            log.info("쿠키가 존재하지 않습니다.");
//            throw new BaseException(RESPONSE_ERROR);
//        }
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.add(JwtAndKakaoProperties.HEADER_STRING, JwtAndKakaoProperties.TOKEN_PREFIX + jwtToken);
//        System.out.println("jwt 토큰값: "+jwtToken);
//
//        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
//        System.out.println("request: "+request);
//        System.out.println("헤더에 추가된 jwt token: "+request.getHeader("Authorization"));
//        System.out.println("헤더에 잘 추가됨");
//    }

    //access token 재발급
    //access token, refresh token을 인자로 받음
    //access token이 만료된 경우만 재발급 가능
    //엑세스 토큰에 있는 user id 이용해서 해당 유저 찾기
    //인자로 받은 refresh token과 db에 있는 refresh token이 일치한지 검사
    //refresh token이 유효한지 검사(토큰 유효성 검사, 만료됬는지 검사)

}
