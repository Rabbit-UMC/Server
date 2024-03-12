package rabbit.umc.com.demo.user.service;

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
import rabbit.umc.com.config.apiPayload.BaseException;
import rabbit.umc.com.config.apiPayload.BaseResponseStatus;
import rabbit.umc.com.config.secret.Secret;
import rabbit.umc.com.demo.base.Status;
import rabbit.umc.com.demo.community.domain.Article;
import rabbit.umc.com.demo.image.service.ImageService;
import rabbit.umc.com.demo.mission.Mission;
import rabbit.umc.com.demo.mission.MissionUserSuccess;
import rabbit.umc.com.demo.mission.MissionUsers;
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
import rabbit.umc.com.demo.user.repository.UserRepository;
import rabbit.umc.com.utils.JwtService;

import javax.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static rabbit.umc.com.config.apiPayload.BaseResponseStatus.*;

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
    private final JwtService jwtService;

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
    public void updateProfile(Long userId, String newNickname, MultipartFile multipartFile) throws BaseException, IOException {
        User user = userRepository.getReferenceById(userId);

        if (!multipartFile.isEmpty()) {
            String newProfileImage = imageService.createImageUrl(multipartFile, "user");
            user.setUserProfileImage(newProfileImage);
        }

        if (!newNickname.isEmpty()) {
            user.setUserName(newNickname);
        }

        userRepository.save(user);
    }

    //유저 프로필 조회
    public UserGetProfileResDto getProfile(Long id) throws BaseException {
        User user = userRepository.getReferenceById(id);
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

    @Transactional
    public ReissueTokenDto reissueTokenIfPossible(String accessToken, String refreshToken) throws BaseException {
        ReissueTokenDto reissueTokenDto = null;
        Long userId = jwtService.getUserIdFromToken(accessToken);
        boolean canReissue = isReissueAllowed(userId, refreshToken);

        if (canReissue) {
            User user = userRepository.getReferenceById(userId);
            String newAccessToken = jwtService.createJwt(Math.toIntExact(userId));
            String newRefreshToken = jwtService.createRefreshToken();
            user.setJwtRefreshToken(newRefreshToken);
            return new ReissueTokenDto(userId, newAccessToken, newRefreshToken);
        } else {
            cannotReissue(Long.valueOf(userId));
            throw new BaseException(INVALID_JWT_REFRESH);
        }
    }

    //access token 재발급
    private boolean isReissueAllowed(Long userId, String refreshToken) throws BaseException {
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
//        User user = findUser(userId);
        User user = userRepository.getReferenceById(userId);
        user.setJwtRefreshToken(token);
        userRepository.save(user);
    }

    public void delRefreshToken(User user) {
        user.setJwtRefreshToken(null);
        userRepository.save(user);
    }

    private void cannotReissue(Long userId) {
//        User user = userService.findUser(Long.valueOf(userId));
        User user = userRepository.getReferenceById(userId);
        delRefreshToken(user);
    }

    @Transactional
    public UserMissionHistoryDto getSuccessMissions(Long userId) {
        int totalCnt = 0; //총 미션 개수

        //user id로 해당 유저가 수행한 미션 리스트를 가져온다.
        List<MissionUsers> missionUsersList = missionUserRepository.getMissionUsersByUserId(userId); //미션 유저
        User user = userRepository.getReferenceById(userId); //유저

        // 성공한 미션을 담기 위한 리스트
        List<Long> successMissionIds = new ArrayList<>();

        List<MissionSchedule> missionSchedules;

        for (MissionUsers missionUser : missionUsersList) {
            int successCnt = 0; //성공한 미션 개수

            //missionUser의 미션 필드를 이용해서 missionSchedules 가져오기
            missionSchedules = missionScheduleRepository.getMissionScheduleByMissionId(missionUser.getMission().getId());

            // missionUser의 미션 필드에 있는 미션이 현재 시각 기준 종료되었다면 가져오기
            Mission mission = missionRepository.getMissionByIdAndEndAtIsBeforeOrderByEndAt(missionUser.getMission().getId(), LocalDateTime.now());

            if (mission != null) { //mission이 현재 시각 기준으로 종료된 경우
                log.info("종료된 미션 번호: " + mission.getId());

                LocalDate currentDate = mission.getStartAt().toLocalDate(); //미션 시작 시각
                LocalDate targetDate = mission.getEndAt().toLocalDate(); //미션 종료 시각
                if (currentDate.isBefore(targetDate)) {
                    // 미션 시작~종료 사이 날짜를 일단위로 계산
                    int targetCnt = (int) ChronoUnit.DAYS.between(currentDate, targetDate);

                    totalCnt++; //총 미션 개수 1개 증가

                    // 미션 시작부터 종료 날짜까지의 모든 날짜
                    List<LocalDate> dateList = getDateBetweenTwoDates(mission.getStartAt(), mission.getEndAt());

                    //해당 미션의 스케줄 리스트
                    for (MissionSchedule missionSchedule : missionSchedules) {
                        Schedule schedule = scheduleRepository.findScheduleById(missionSchedule.getSchedule().getId());

                        //스케줄 종료 시간을 LocalDate 자료형으로
                        String whenStr = schedule.getEndAt().toString().substring(0, 10);
                        LocalDate when = LocalDate.parse(whenStr);

//                    System.out.println("유저가 스케줄을한 endAt 날짜: " + when);
//                    System.out.println("스케줄로 얻은 유저 번호: " + schedule.getUser().getId());
//                    System.out.println("매개변수로 얻은 유저 번호: " + userId);

                        Long userIdinSchedule = schedule.getUser().getId();
                        //스케줄의 user id가 조회한 user의 id와 같고
                        // 미션 시작부터 종료 날짜까지의 모든 날짜 리스트 안에 스케줄 종료 시간이 있다면
                        if (userIdinSchedule.equals(userId) && dateList.contains(when)) {
                            successCnt++; //성공 카운트 1개 올라감
                            log.info("스케줄 하루 성공");
                        }


                    }
                    int successCondition = targetCnt + 1;
//                System.out.println("위 미션의 성공 조건: " + successCondition + "일 스케줄 성공");
//                System.out.println("유저가 스케줄을 성공한 일 수: " + successCnt);

                    if (successCnt == successCondition) {
                        log.info("성공한 미션 번호: {}", missionUser.getMission().getId());
                        successMissionIds.add(missionUser.getMission().getId());

                        MissionUserSuccess findMissionUserSuccess = missionUserSuccessRepository.getMissionUserSuccessByMissionIdAndUserId(mission.getId(), userId);

                        if (findMissionUserSuccess == null) {
                            log.info("DB에 저장되어 있지 않은 성공한 미션을 missionUserSuccess 테이블에 저장합니다.");
                            MissionUserSuccess missionUserSuccess = new MissionUserSuccess();
                            missionUserSuccess.setUser(user);
                            missionUserSuccess.setMission(mission);
                            missionUserSuccessRepository.save(missionUserSuccess);
                        }

                    }
                } else {
                    log.warn("미션의 start 시간이 end 시간보다 이전입니다.");
                    log.warn("mission start: {}, mission end: {}", currentDate, targetDate);
                }
            }
        }
        List<Mission> missionList = missionRepository.getMissionsByIdIsIn(successMissionIds);
        List<UserMissionResDto> resultList = missionList.stream()
                .map(UserMissionResDto::toUserMissionResDto)
                .collect(Collectors.toList());

        UserMissionHistoryDto result = UserMissionHistoryDto.toSuccessMissionHistoryRes(totalCnt, resultList);
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

        //user id로 해당 유저가 수행한 미션 리스트를 가져온다.
        List<MissionUsers> missionUsersList = missionUserRepository.getMissionUsersByUserId(userId);
        List<Long> failureMissionIds = new ArrayList<>();

        List<MissionSchedule> missionSchedules;

        for (MissionUsers missionUser : missionUsersList) {
            int successCnt = 0;
            missionSchedules = missionScheduleRepository.getMissionScheduleByMissionId(missionUser.getMission().getId());

            Mission mission = missionRepository.getMissionByIdAndEndAtIsBefore(missionUser.getMission().getId(), LocalDateTime.now());
            if (mission != null) {
                log.info("종료된 미션 번호: " + mission.getId());

                LocalDate currentDate = mission.getStartAt().toLocalDate();
                LocalDate targetDate = mission.getEndAt().toLocalDate();

                if (currentDate.isBefore(targetDate)) {
                    mission.setMissionUserSuccessList(missionUserSuccessRepository.getMissionUserSuccessByMissionId(mission.getId()));

                    targetCnt = (int) ChronoUnit.DAYS.between(currentDate, targetDate); // 현재 날짜와 대상 날짜 사이의 일 수 계산

                    totalCnt++;

                    //해당 미션의 시작 날짜부터 종료 날짜까지의 모든 날짜를 가져옴
                    List<LocalDate> dateList = getDateBetweenTwoDates(mission.getStartAt(), mission.getEndAt());

                    for (MissionSchedule missionSchedule : missionSchedules) {
                        Schedule schedule = scheduleRepository.findScheduleById(missionSchedule.getSchedule().getId());
                        String whenStr = schedule.getEndAt().toString().substring(0, 10);
                        LocalDate when = LocalDate.parse(whenStr);

                        //스케줄 종료 시간을 LocalDate 자료형으로
//                        System.out.println("유저가 스케줄을한 endAt 날짜: " + when);
//                        System.out.println("스케줄로 얻은 유저 번호: " + schedule.getUser().getId());
//                        System.out.println("매개변수로 얻은 유저 번호: " + userId);
                        Long userIdinSchedule = schedule.getUser().getId();
                        //스케줄의 user id가 조회한 user의 id와 같고
                        // 미션 시작부터 종료 날짜까지의 모든 날짜 리스트 안에 스케줄 종료 시간이 있다면
                        if (userIdinSchedule.equals(userId) && dateList.contains(when)) {
                            successCnt++;
                            log.info("스케줄 하루 성공(성공 카운트 1개 올라감)");
                        }
                    }

                    if (successCnt < targetCnt + 1) {
                        log.info("미션 실패!!!");
                        failureMissionIds.add(missionUser.getMission().getId());
                    } else {
                        log.info("미션 성공~~~~~~~~");
                    }
                } else {
                    log.warn("미션의 start 시간이 end 시간보다 이전입니다.");
                    log.warn("mission start: {}, mission end: {}", currentDate, targetDate);
                }
            }
        }
        List<Mission> missionList = missionRepository.getMissionsByIdIsIn(failureMissionIds);
        List<UserMissionResDto> resultList = missionList.stream()
                .map(UserMissionResDto::toUserMissionResDto)
                .collect(Collectors.toList());

        UserMissionHistoryDto result = UserMissionHistoryDto.toFailMissionHistoryRes(totalCnt, resultList);
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
        System.out.println("user id: " + userId);

        User user = userRepository.getReferenceById(new Long(userId));
        if (user == null) {
            new UsernameNotFoundException("회원번호 " + userId + " 님을 찾을 수 없습니다.");
        }
        log.info("유저 권한: {}", user.getUserPermission());

        return new UserDetailsImpl(user);
    }

    @Transactional
    public boolean isUserValid(Long userId) {
        User user = userRepository.getReferenceById(userId);

        if (user.getStatus() == Status.INACTIVE || user.getStatus() == Status.LOGGED_OUT) {
            return false;
        } else {
            return true;
        }
    }

    public boolean isValidException(Long userId) throws BaseException {
        if (isUserValid(userId)) {
            return true;
        } else {
            throw new BaseException(UNAUTHORIZED_USER);
        }
    }

}
