package rabbit.umc.com.demo.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import rabbit.umc.com.config.BaseException;
import rabbit.umc.com.config.BaseResponseStatus;
import rabbit.umc.com.demo.article.ArticleRepository;
import rabbit.umc.com.demo.article.domain.Article;
import rabbit.umc.com.demo.article.dto.ArticleListRes;
import rabbit.umc.com.demo.user.Domain.User;
import rabbit.umc.com.demo.user.Dto.UserEmailNicknameDto;
import rabbit.umc.com.demo.user.Dto.UserGetProfileResDto;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static rabbit.umc.com.config.BaseResponseStatus.POST_USERS_INVALID_EMAIL;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;
    ArticleRepository articleRepository;

    public User findUser(Long id) throws BaseException {
        Optional<User> optionalUser = userRepository.findById(id);
        User user = optionalUser.orElseThrow(() -> new BaseException(BaseResponseStatus.RESPONSE_ERROR));
        return user;
    }

    public void getEmailandNickname(UserEmailNicknameDto userEmailNicknameReqDto) throws BaseException {
        //해당 유저 아이디 먼저 찾고
//        Optional<User> optionalUser = userRepository.findById(userEmailNicknameReqDto.getId());
//        User user = optionalUser.orElseThrow(() -> new BaseException(BaseResponseStatus.RESPONSE_ERROR));
        User user = findUser(userEmailNicknameReqDto.getId());
        user.setUserName(userEmailNicknameReqDto.getUserName());
        user.setUserEmail(userEmailNicknameReqDto.getUserEmail());
        //해당 유저 엔티티에 나머지값 업데이트
        userRepository.save(user);
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
            throw new BaseException(POST_USERS_INVALID_EMAIL);
        }
    }

    //프로필 이미지 수정
    @Transactional
    public void updateProfileImage(Long id, String newProfileImage) throws BaseException {
        userRepository.updateUserUserProfileImageById(id, newProfileImage);
    }

    //닉네임 수정
    @Transactional
    public void updateNickname(Long id, String newNickname){
        userRepository.updateUserUserNameById(id, newNickname);
    }

    //유저 프로필 조회
    public UserGetProfileResDto getProfile(Long id) throws BaseException {
        User user = findUser(id);
        UserGetProfileResDto userGetProfileResDto = new UserGetProfileResDto(user.getUserEmail(), user.getUserName(), user.getUserProfileImage());
        return userGetProfileResDto;
    }

    public List<ArticleListRes> getArticles(int page, Long userId) {
        //user id로 article id찾기
//        Long articleId = UserRepository.findArticleIdsByUserId(userId);
//        ArticleRepository.findArticleById(articleId);
//        //article 객체 찾고
//        Article article = articleRepository.findArticleById(articleId);
        //dto에 필요한 것만 넣어서 반환
        //최신순으로 상단에서 추가(생성일 기준으로 모든 글 내림차순으로)

        int pageSize = 20;

        PageRequest pageRequest = PageRequest.of(page, pageSize, Sort.by("createdAt").descending());

        List<Article> articlePage = userRepository.findArticlesByUserIdOrderByCreatedAtDesc(userId, pageRequest);

        List<ArticleListRes> articleListRes = articlePage.stream()
                .map(ArticleListRes::toArticleListRes)
                .collect(Collectors.toList());

        return articleListRes;
    }

    public List<ArticleListRes> getCommentedArticles(int page, Long userId) {
        int pageSize = 20;

        PageRequest pageRequest = PageRequest.of(page, pageSize, Sort.by("createdAt").descending());

        List<Article> articlePage = userRepository.findCommentedArticlesByUserId(userId, pageRequest);

        List<ArticleListRes> articleListRes = articlePage.stream()
                .map(ArticleListRes::toArticleListRes)
                .collect(Collectors.toList());

        return articleListRes;
    }

    //카테고리별 랭킹
    public Long getRank(Long userId, Long categoryId){
        //먼저 해당 카테고리의 메인 미션 유저인지 확인
        //mainMissionUser 값들 중에 해당 userId랑 일치한 값이 있는지
        Boolean isMainMissionUser = userRepository.existsMainMissionUserByUserIdAndCategoryId(userId, categoryId);

        //순위 확인
        Long ranking = userRepository.getRankByScoreForMainMissionByUserIdAndCategoryId(userId, categoryId);
        return ranking;
    }
}
