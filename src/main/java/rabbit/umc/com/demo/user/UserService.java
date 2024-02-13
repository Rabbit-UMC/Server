package rabbit.umc.com.demo.user;

import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import rabbit.umc.com.config.BaseException;
import rabbit.umc.com.config.BaseResponseStatus;
import rabbit.umc.com.config.secret.Secret;
import rabbit.umc.com.demo.Status;
import rabbit.umc.com.demo.community.domain.Article;
import rabbit.umc.com.demo.image.ImageService;
import rabbit.umc.com.demo.mission.Mission;
import rabbit.umc.com.demo.mission.MissionUserSuccess;
import rabbit.umc.com.demo.mission.MissionUsers;
import rabbit.umc.com.demo.mission.dto.MissionHistoryRes;
import rabbit.umc.com.demo.mission.dto.MissionHomeRes;
import rabbit.umc.com.demo.mission.repository.MissionRepository;
import rabbit.umc.com.demo.mission.repository.MissionUserSuccessRepository;
import rabbit.umc.com.demo.mission.repository.MissionUsersRepository;
import rabbit.umc.com.demo.schedule.domain.MissionSchedule;
import rabbit.umc.com.demo.schedule.domain.Schedule;
import rabbit.umc.com.demo.schedule.repository.MissionScheduleRepository;
import rabbit.umc.com.demo.schedule.repository.ScheduleRepository;
import rabbit.umc.com.demo.user.Domain.User;
import rabbit.umc.com.demo.user.Domain.UserDetailsImpl;
import rabbit.umc.com.demo.user.Domain.UserPermission;
import rabbit.umc.com.demo.user.Dto.*;

import javax.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static rabbit.umc.com.config.BaseResponseStatus.*;
import static rabbit.umc.com.demo.Status.ACTIVE;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final MissionRepository missionRepository;
    private final MissionUsersRepository missionUserRepository;
    private final MissionScheduleRepository missionScheduleRepository;
    private final MissionUserSuccessRepository missionUserSuccessRepository;
    private final ScheduleRepository scheduleRepository;
    private final ImageService imageService;

    //유저 아이디로 User 객체 찾기
    public User findUser(Long id) throws BaseException {
        User user = userRepository.getReferenceById(id);
        if (user == null) {
            log.info("데이터 베이스에서 찾을 수 없는 user id입니다.");
            throw new BaseException(BaseResponseStatus.USERS_EMPTY_USER_ID);
        } else if (user.getStatus() == Status.INACTIVE) {
            log.info("탈퇴한 회원입니다.");
            throw new BaseException(BaseResponseStatus.INVALID_USER_ID);
        }
        return user;
    }


    public boolean isExistSameNickname(String nickname, Long jwtUserId) throws BaseException {
        //본인을 제외하고, 같은 닉네임이 있는지 확인

        boolean existSameName = userRepository.existsByNicknameAndNotUserId(nickname, jwtUserId);
        if (existSameName) {
            log.info("중복된 닉네임입니다.");
            System.out.println("중복된 닉네임: " + nickname);
            return true;
        }
        return false;
    }

    public boolean isExistSameNicknameWithoutUserId(String nickname) throws BaseException {
        //전체 DB에서 같은 닉네임이 있는지 확인
        boolean existSameName = userRepository.existsByUserName(nickname);
        if (existSameName) {
            log.info("이미 DB에 존재하는 닉네임입니다.");
            return true;
        }
        return false;
    }


    //닉네임, 프로필 이미지 수정
    @Transactional
    public void updateProfile(Long userId, String newNickname, /*String*/MultipartFile multipartFile) throws BaseException, IOException {
        User user = findUser(userId);
        String newProfileImage = imageService.getImageUrl(multipartFile, "user");
        user.setUserProfileImage(newProfileImage);
        user.setUserName(newNickname);
        userRepository.save(user);
    }

    //유저 프로필 조회
    public UserGetProfileResDto getProfile(Long id) throws BaseException {
        User user = findUser(id);
        UserGetProfileResDto userGetProfileResDto = new UserGetProfileResDto(user.getUserName(), user.getUserProfileImage(), user.getCreatedAt());
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

        List<Article> articlePage = userRepository.findCommentedArticlesByUserId(userId, pageRequest);

        List<UserArticleListResDto> userArticleListResDtos = articlePage.stream()
                .map(UserArticleListResDto::toArticleListRes)
                .collect(Collectors.toList());

        return userArticleListResDtos;
    }

    //access token 재발급
    public boolean isReissueAllowed(Long userId, String refreshToken) throws BaseException {
        //인자로 받은 refresh token과 해당 user id의db에 있는 refresh token이 일치한지 검사
        boolean tokenMatch = userRepository.checkJwtRefreshTokenMatch(userId, refreshToken);
        if (tokenMatch) {
            try {
                Jwts.parser()
                        .setSigningKey(Secret.JWT_SECRET_KEY) // 서명 키 지정
                        .parseClaimsJws(refreshToken);
            } catch (Exception ex) {
                log.info("refresh token이 유효하지 않습니다.");
                return false;
            }
        } else {
            log.info("데이터베이스의 리프레시 토큰과 일치하지 않습니다.");
            throw new BaseException(INVALID_JWT_REFRESH);
        }
        return true;
    }

    //refresh token db에 저장
    public void saveRefreshToken(Long userId, String token) throws BaseException {
        User user = findUser(userId);
        user.setJwtRefreshToken(token);
        userRepository.save(user);
    }

    public void delRefreshToken(User user) {
        user.setJwtRefreshToken(null);
        userRepository.save(user);
    }

    @Transactional
    public UserMissionHistoryDto getSuccessMissions(Long userId) {
//        Status status = ACTIVE;
        LocalDateTime now = LocalDateTime.now();
        int totalCnt = 0;

        // 내가 참가한 미션들 가져오기
        List<MissionUsers> missionUsersList = missionUserRepository.getMissionUsersByUserId(userId);
        User user = userRepository.getReferenceById(userId);

        // 미션 인덱스들 담기 위한 리스트
        List<Long> ids = new ArrayList<>();

        List<MissionSchedule> missionSchedules;

        //user id로 해당 유저가 수행한 미션 리스트를 가져온다.
//        그 미션 리스트중, 하나를 가져와서 해당 미션이 현재 종료 시각이 지났다면 mission에 넣는다.


        for (MissionUsers missionUser : missionUsersList) {
            int successCnt = 0;
            missionSchedules = missionScheduleRepository.getMissionScheduleByMissionId(missionUser.getMission().getId());

            // 1명의 종료된 미션
            Mission mission = missionRepository.getMissionByIdAndEndAtIsBeforeOrderByEndAt(missionUser.getMission().getId(), now);


            if (mission != null) {
                System.out.println("종료된 미션 번호: " + mission.getId());
                LocalDate targetDate = mission.getEndAt().toLocalDate();
                LocalDate currentDate = mission.getStartAt().toLocalDate();
//                int targetCnt; // 현재 날짜와 대상 날짜 사이의 일 수 계산
                int targetCnt = (int) ChronoUnit.DAYS.between(currentDate, targetDate);
                totalCnt++;
                System.out.println("종료된 미션의 end_At: " + targetDate);
                System.out.println("종료된 미션의 start_at: " + currentDate);

                // 미션 시작부터 종료 날짜까지의 날짜들
                List<LocalDate> dateList = getDateBetweenTwoDates(mission.getStartAt(), mission.getEndAt());
                System.out.println("미션 시작~종료까지 날짜들: " + dateList);

                //해당 미션의 스케줄 리스트
                for (int i = 0; i < missionSchedules.size(); i++) {
                    Schedule schedule = scheduleRepository.findScheduleById(missionSchedules.get(i).getSchedule().getId());
                    String whenStr = schedule.getEndAt().toString().substring(0, 10);
                    LocalDate when = LocalDate.parse(whenStr);
                    System.out.println("유저가 스케줄을한 endAt 날짜: " + when);

                    System.out.println("스케줄로 얻은 유저 번호: " + schedule.getUser().getId());
                    System.out.println("매개변수로 얻은 유저 번호: " + userId);

                    Long userIdinSchedule = schedule.getUser().getId();
                    //if(userIdinSchedule == userId){
                    System.out.println("스케줄로 얻은 유저 번호랑 매개변수로 얻은 유저 번호랑 같음");
                    if (dateList.contains(when)) {
                        System.out.println("스케줄 한 날이 미션 시작~종료 날짜 안에 포함된다.");
                        successCnt++;
                    }
                    //}
                }
                int successCondition = targetCnt + 1;
                System.out.println("위 미션의 성공 조건: " + successCondition + "일 스케줄 성공");
                System.out.println("유저가 스케줄을 성공한 일 수: " + successCnt);

                if (successCnt == targetCnt + 1) {
                    System.out.println("ids에 추가되는 미션 번호: " + missionUser.getMission().getId());
                    ids.add(missionUser.getMission().getId());

                    MissionUserSuccess findMissionUserSuccess = missionUserSuccessRepository.getMissionUserSuccessByMissionIdAndUserId(mission.getId(), userId);

                    if (findMissionUserSuccess == null) {
                        System.out.println("성공한 미션인데 missionUserSuccess 테이블에 저장이 안되어 있어서 저장한다.");
                        MissionUserSuccess missionUserSuccess = new MissionUserSuccess();
                        missionUserSuccess.setUser(user);
                        missionUserSuccess.setMission(mission);
                        missionUserSuccessRepository.save(missionUserSuccess);
                    }

                }
            }
        }

//        List<Mission> missionList = missionRepository.getMissionsByIdIsIn(ids);
        List<Mission> missionList = missionRepository.getMissionsByIdIsIn(ids);
        List<UserMissionResDto> resultList = missionList.stream()
                .map(UserMissionResDto::toUserMissionResDto)
                .collect(Collectors.toList());

        UserMissionHistoryDto result = UserMissionHistoryDto.toSuccessMissionHistoryRes(totalCnt, resultList);

        totalCnt = 0;

        return result;
    }


    /**
     * 도전 실패한 미션리스트
     *
     * @param userId
     * @return
     */
    public UserMissionHistoryDto getFailureMissions(Long userId) {
        int targetCnt = 0;
        int totalCnt = 0;
//        Status status = ACTIVE;

        List<MissionUsers> missionUsersList = missionUserRepository.getMissionUsersByUserId(userId);

        List<Long> ids = new ArrayList<>();


        List<MissionSchedule> missionSchedules;
        LocalDateTime now = LocalDateTime.now();


        for (MissionUsers missionUser : missionUsersList) {
            int successCnt = 0;
            missionSchedules = missionScheduleRepository.getMissionScheduleByMissionId(missionUser.getMission().getId());


            Mission mission = missionRepository.getMissionByIdAndEndAtIsBefore(missionUser.getMission().getId(), now);

            if (mission != null) {
                mission.setMissionUserSuccessList(missionUserSuccessRepository.getMissionUserSuccessByMissionId(mission.getId()));

                LocalDate targetDate = mission.getEndAt().toLocalDate();
                LocalDate currentDate = mission.getStartAt().toLocalDate();
                targetCnt = (int) ChronoUnit.DAYS.between(currentDate, targetDate); // 현재 날짜와 대상 날짜 사이의 일 수 계산
                totalCnt++;


                //해당 미션의 시작 날짜부터 종료 날짜까지의 모든 날짜를 가져옴
                List<LocalDate> dateList = getDateBetweenTwoDates(mission.getStartAt(), mission.getEndAt());

                for (MissionSchedule ms : missionSchedules) {
                    Schedule schedule = scheduleRepository.findScheduleById(ms.getSchedule().getId());
                    String whenStr = schedule.getEndAt().toString().substring(0, 10);
                    LocalDate when = LocalDate.parse(whenStr);

                    if (dateList.contains(when)) {
                        successCnt++;
                    }
                }

                if (successCnt < targetCnt + 1) {
                    ids.add(missionUser.getMission().getId());
                }
            }
        }

//        List<Mission> missionList = missionRepository.getMissionsByIdIsIn(ids);
//        List<MissionHomeRes> resultList = missionList.stream()
//                .map(MissionHomeRes::toMissionHomeRes)
//                .collect(Collectors.toList());

        List<Mission> missionList = missionRepository.getMissionsByIdIsIn(ids);
        List<UserMissionResDto> resultList = missionList.stream()
                .map(UserMissionResDto::toUserMissionResDto)
                .collect(Collectors.toList());

        UserMissionHistoryDto result = UserMissionHistoryDto.toFailMissionHistoryRes(totalCnt, resultList);

        totalCnt = 0;

        return result;
    }

    //시작 날짜부터 종료 날짜까지의 모든 날짜를 리스트로 반환
    public static List<LocalDate> getDateBetweenTwoDates(LocalDateTime startAt, LocalDateTime endAt) {
        LocalDate startDate = startAt.toLocalDate();
        LocalDate endDate = endAt.toLocalDate();

        int numOfDaysBetween = (int) ChronoUnit.DAYS.between(startDate, endDate);

        return IntStream.iterate(0, i -> i <= numOfDaysBetween, i -> i + 1)
                .mapToObj(i -> startDate.plusDays(i))
                .collect(Collectors.toList());
    }

    @Transactional
    public void changePermissionToUser(User user) {
        user.setUserPermission(UserPermission.USER);
        userRepository.save(user);
    }

    @Transactional
    public void changePermissionToHost(User user) {
        user.setUserPermission(UserPermission.HOST);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {

        User user = userRepository.getReferenceById(new Long(userId));
        if (user == null) {
            new UsernameNotFoundException("회원번호 " + userId + " 님을 찾을 수 없습니다.");
        }
        log.info("유저 권한: {}", user.getUserPermission());

        return new UserDetailsImpl(user);
    }
}
