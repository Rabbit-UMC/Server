package rabbit.umc.com.demo.mainmission;

import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rabbit.umc.com.config.BaseException;
import rabbit.umc.com.demo.community.category.CategoryRepository;
import rabbit.umc.com.demo.community.domain.Category;
import rabbit.umc.com.demo.converter.MainMissionConverter;
import rabbit.umc.com.demo.mainmission.domain.mapping.LikeMissionProof;
import rabbit.umc.com.demo.mainmission.domain.MainMission;
import rabbit.umc.com.demo.mainmission.domain.mapping.MainMissionProof;
import rabbit.umc.com.demo.mainmission.domain.mapping.MainMissionUsers;
import rabbit.umc.com.demo.mainmission.dto.GetMainMissionRes;
import rabbit.umc.com.demo.mainmission.dto.GetMainMissionRes.MissionProofImageDto;
import rabbit.umc.com.demo.mainmission.dto.GetMainMissionRes.RankDto;
import rabbit.umc.com.demo.mainmission.dto.PostMainMissionReq;
import rabbit.umc.com.demo.mainmission.repository.LikeMissionProofRepository;
import rabbit.umc.com.demo.mainmission.repository.MainMissionProofRepository;
import rabbit.umc.com.demo.mainmission.repository.MainMissionRepository;
import rabbit.umc.com.demo.mainmission.repository.MainMissionUsersRepository;
import rabbit.umc.com.demo.report.Report;
import rabbit.umc.com.demo.report.ReportRepository;
import rabbit.umc.com.demo.user.Domain.User;
import rabbit.umc.com.demo.user.UserRepository;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import rabbit.umc.com.utils.DateUtil;

import static rabbit.umc.com.config.BaseResponseStatus.*;
import static rabbit.umc.com.demo.Status.*;
import static rabbit.umc.com.demo.user.Domain.UserPermision.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MainMissionService {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final MainMissionRepository mainMissionRepository;
    private final MainMissionProofRepository mainMissionProofRepository;
    private final UserRepository userRepository;
    private final LikeMissionProofRepository likeMissionProofRepository;
    private final ReportRepository reportRepository;
    private final CategoryRepository categoryRepository;
    private final MainMissionUsersRepository mainMissionUsersRepository;

    public GetMainMissionRes getMainMission(Long mainMissionId, int day, Long userId) throws BaseException {
        try {
            MainMission mainMission = mainMissionRepository.getReferenceById(mainMissionId);
            User user = userRepository.getReferenceById(userId);

            // 해당하는 일차의 인증 사진 가져오기
            LocalDateTime startDate = mainMission.getStartAt().atStartOfDay();
            LocalDateTime targetDate = startDate.plusDays(day - 1);
            LocalDateTime endDate = targetDate.plusDays(1);
            List<MainMissionProof> mainMissionProofs = mainMissionProofRepository.findAllByMainMissionIdAndCreatedAtBetween(mainMissionId, targetDate, endDate);

            // DTO 매핑
            List<MissionProofImageDto> missionProofImages = MainMissionConverter.toMissionProofImageDto(mainMissionProofs);

            //JWT 유저가 좋아요한 인증사진 가져오기
            List<LikeMissionProof> likeMissionProofs = likeMissionProofRepository.findLikeMissionProofByUser(user);

            // DTO에 매핑된 해당 일차 인증 사진에 대해 user가 좋아한 사진이면 isLike = ture 설정
            for (MissionProofImageDto imageDto : missionProofImages) {
                boolean isLiked = likeMissionProofs
                        .stream()
                        .anyMatch(likeProof -> likeProof.getMainMissionProof().getId().equals(imageDto.getImageId()));
                if (isLiked) {
                    imageDto.setIsLike();
                }
            }

            //mainMissionId 메인 미션 랭킹 가져오기
            List<MainMissionUsers> top3 = mainMissionUsersRepository.findTop3OByMainMissionIdOrderByScoreDesc(mainMissionId);
            List<RankDto> rankList = top3.stream()
                    .map(mainMissionUsers -> RankDto.builder()
                            .userId(mainMissionUsers.getId())
                            .userName(mainMissionUsers.getUser().getUserName())
                            .build())
                    .collect(Collectors.toList());

            //Res DTO 에 매핑
            return GetMainMissionRes.builder()
                    .mainMissionId(mainMission.getId())
                    .mainMissionName(mainMission.getTitle())
                    .startDay(mainMission.getStartAt().format(DATE_TIME_FORMATTER))
                    .dDay(DateUtil.getMissionDday(mainMission.getEndAt()))
                    .mainMissionContent(mainMission.getContent())
                    .rank(rankList)
                    .missionProofImages(missionProofImages)
                    .build();

        } catch (EntityNotFoundException e) {
            throw new BaseException(DONT_EXIST_MISSION);
        }
    }


    @Transactional
    public void likeMissionProof(Long userId, Long mainMissionProofId) throws BaseException {
        try {
            MainMissionProof mainMissionProof = mainMissionProofRepository.getReferenceById(mainMissionProofId);
            // 인증 사진 존재 체크
            if (mainMissionProof.getProofImage() == null) {
                throw new EntityNotFoundException("Unable to find proofId with id:" + mainMissionProofId);
            }

            User user = userRepository.getReferenceById(userId);
            LikeMissionProof findLikeMissionProof = likeMissionProofRepository.findLikeMissionProofByUserAndMainMissionProofId(user, mainMissionProofId);
            //이미 좋아한 인증 사진인지 체크
            if (findLikeMissionProof != null) {
                throw new BaseException(FAILED_TO_LIKE_MISSION);
            }

            // 좋아요 1점 추가 로직
            MainMissionUsers missionUsers = mainMissionUsersRepository.findMainMissionUsersByUserAndAndMainMission(mainMissionProof.getUser(), mainMissionProof.getMainMission());
            missionUsers.addLikeScore();
            mainMissionUsersRepository.save(missionUsers);

            //좋아요 여부 저장
            LikeMissionProof likeMissionProof = new LikeMissionProof();
            likeMissionProof.setLikeMissionProof(user, mainMissionProof);
            likeMissionProofRepository.save(likeMissionProof);

        } catch (EntityNotFoundException e) {
            throw new BaseException(DONT_EXIST_MISSION_PROOF);
        }
    }

    @Transactional
    public void unLikeMissionProof(Long userId, Long mainMissionProofId) throws BaseException {
        try {
            MainMissionProof mainMissionProof = mainMissionProofRepository.getReferenceById(mainMissionProofId);
            //인증사진 존재 체크
            if (mainMissionProof.getProofImage() == null) {
                throw new EntityNotFoundException("Unable to find proofId with id:" + mainMissionProofId);
            }
            User user = userRepository.getReferenceById(userId);
            LikeMissionProof findLikeMissionProof = likeMissionProofRepository.findLikeMissionProofByUserAndMainMissionProofId(user, mainMissionProofId);
            // 좋아요하지 않은 인증 사진인지 체크
            if (findLikeMissionProof == null) {
                throw new BaseException(FAILED_TO_UNLIKE_MISSION);
            }

            //1점 삭제 로직
            MainMissionUsers missionUsers = mainMissionUsersRepository.findMainMissionUsersByUserAndAndMainMission(mainMissionProof.getUser(), mainMissionProof.getMainMission());
            missionUsers.unLikeScore();
            mainMissionUsersRepository.save(missionUsers);

            likeMissionProofRepository.delete(findLikeMissionProof);
        } catch (EntityNotFoundException e) {
            throw new BaseException(DONT_EXIST_MISSION_PROOF);
        }
    }


    @Transactional
    public void reportMissionProof(Long userId, Long mainMissionProofId) throws BaseException {
        try {
            MainMissionProof mainMissionProof = mainMissionProofRepository.getReferenceById(mainMissionProofId);
            //존재하는 인증 사진인지 체크
            if (mainMissionProof.getProofImage() == null) {
                throw new EntityNotFoundException("Unable to find proofId with id:" + mainMissionProofId);
            }
            User user = userRepository.getReferenceById(userId);
            Report findReport = reportRepository.findReportByUserIdAndAndMainMissionProofId(userId, mainMissionProofId);
            //이미 신고한 사진인지 체크
            if (findReport != null) {
                throw new BaseException(FAILED_TO_REPORT);
            }
            //신고 저장
            Report report = new Report();
            report.setReport(user,mainMissionProof);
            reportRepository.save(report);

            //신고 횟수 15회 이상시 비활성화 처리
            List<Report> countReport = reportRepository.findAllByMainMissionProofId(mainMissionProofId);
            if (countReport.size() > 14) {
                mainMissionProof.inActive();
            }

        } catch (EntityNotFoundException e) {
            throw new BaseException(DONT_EXIST_MISSION_PROOF);
        }

    }

    @Transactional
    public void createMainMission(Long userId, Long categoryId, PostMainMissionReq postMainMissionReq) throws BaseException {

        //유저 자격[HOST] 확인
        User user = userRepository.getReferenceById(userId);
        if (user.getUserPermission() != HOST) {
            throw new BaseException(INVALID_JWT);
        }

        //해당 카테고리 자격 확인
        Category category = categoryRepository.getReferenceById(categoryId);
        if (category.getUserId() != userId) {
            throw new BaseException(INVALID_JWT);
        }


        MainMission lastMission = mainMissionRepository.findMainMissionByCategoryAndStatus(category, ACTIVE);

        // 아전미션이 끝나지 않았다면 새 미션 생성 불가능
        if (lastMission.getEndAt().isAfter(LocalDate.now()) ){
            throw new BaseException(NOT_DONE_MISSION);
        }

        //해당 카테고리 이전 미션 존재 시 이전 미션은 비활성화
        if (lastMission != null) {
            lastMission.inActive();
            mainMissionRepository.save(lastMission);
        }

        //메인 미션 생성
        MainMission newMainMission = new MainMission();
        newMainMission.setMainMission(postMainMissionReq, category);
        mainMissionRepository.save(newMainMission);

    }

    @Transactional
    public void uploadProofImage(Long categoryId, Long userId, String filePath) throws BaseException {
        MainMission mainMission = mainMissionRepository.findMainMissionByCategoryIdAndStatus(categoryId, ACTIVE);
        User user = userRepository.getReferenceById(userId);

        // 메인 미션 참여 아직 안했으면 참여 시키기
        MainMissionUsers findUser = mainMissionUsersRepository.findMainMissionUsersByUserAndAndMainMission(user, mainMission);
        if (findUser == null) {
            MainMissionUsers mainMissionUsers = new MainMissionUsers();
            mainMissionUsers.setMainMissionUsers(user, mainMission);
            mainMissionUsersRepository.save(mainMissionUsers);
        }

        //만약 당일 이미 사진을 올렸으면 리젝
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDateTime.now().toLocalDate().atTime(LocalTime.MAX);
        List<MainMissionProof> proof = mainMissionProofRepository.findAllByUserAndCreatedAtBetween(user, startOfDay, endOfDay);
        if (!proof.isEmpty()) {
            throw new BaseException(FAILED_TO_UPLOAD);
        }

        // 10점 점수 획득
        MainMissionUsers missionUsers = mainMissionUsersRepository.findMainMissionUsersByUserAndAndMainMission(user, mainMission);
        missionUsers.addProofScore();
        mainMissionUsersRepository.save(missionUsers);

        //인증 사진 저장
        MainMissionProof saveProof = new MainMissionProof();
        saveProof.setMainMissionProof(filePath, user, mainMission);
        mainMissionProofRepository.save(saveProof);

    }


    /**
     * 스케줄러
     * 묘방생 미션 종료시 권한 수정됨
     */
    @Transactional
//    @Scheduled(cron = "0 * * * * ?")
    @Scheduled(cron = "0 0 0 * * ?") // 매일 자정에 스케줄 실행
    public void checkCompletedMainMissions() {
        List<MainMission> completedMissions = mainMissionRepository.findMainMissionsByEndAtBeforeAndLastMissionTrue(LocalDate.now());
        for (MainMission mainMission : completedMissions) {
            List<MainMissionUsers> topScorers = mainMissionUsersRepository.findTopScorersByMainMissionOrderByScoreDesc(mainMission, PageRequest.of(0, 1));

            if (!topScorers.isEmpty()) {
                //이전 묘집사 강등
                Long beforeUserId = mainMission.getCategory().getUserId();
                User beforeUser = userRepository.getReferenceById(beforeUserId);
                beforeUser.setUserPermission(USER);

                MainMissionUsers topScorer = topScorers.get(0);
                //유저 권한 변경
                User user = topScorer.getUser();
                user.setUserPermission(HOST);
                userRepository.save(user);
                //해당 카테고리 묘집사 변경
                Category category = mainMission.getCategory();
                category.setUserId(user.getId());

                mainMission.setLastMission(Boolean.FALSE);
            }
        }
    }
}



