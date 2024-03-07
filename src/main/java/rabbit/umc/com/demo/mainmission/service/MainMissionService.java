package rabbit.umc.com.demo.mainmission.service;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import rabbit.umc.com.config.apiPayload.BaseException;
import rabbit.umc.com.demo.community.category.CategoryRepository;
import rabbit.umc.com.demo.community.category.CategoryService;
import rabbit.umc.com.demo.community.domain.Category;
import rabbit.umc.com.demo.converter.RankConverter;
import rabbit.umc.com.demo.converter.ReportConverter;
import rabbit.umc.com.demo.image.service.ImageService;
import rabbit.umc.com.demo.mainmission.domain.mapping.LikeMissionProof;
import rabbit.umc.com.demo.mainmission.domain.MainMission;
import rabbit.umc.com.demo.mainmission.domain.mapping.MainMissionProof;
import rabbit.umc.com.demo.mainmission.domain.mapping.MainMissionUsers;
import rabbit.umc.com.demo.mainmission.dto.GetMainMissionRes;
import rabbit.umc.com.demo.mainmission.dto.GetMainMissionRes.MissionProofImageDto;
import rabbit.umc.com.demo.mainmission.dto.GetMainMissionRes.RankDto;
import rabbit.umc.com.demo.mainmission.dto.MainMissionViewRes;
import rabbit.umc.com.demo.mainmission.dto.PostMainMissionReq;
import rabbit.umc.com.demo.mainmission.repository.LikeMissionProofRepository;
import rabbit.umc.com.demo.mainmission.repository.MainMissionProofRepository;
import rabbit.umc.com.demo.mainmission.repository.MainMissionRepository;
import rabbit.umc.com.demo.mainmission.repository.MainMissionUsersRepository;
import rabbit.umc.com.demo.report.ReportService;
import rabbit.umc.com.demo.user.Domain.User;
import rabbit.umc.com.demo.user.service.UserQueryService;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;
import rabbit.umc.com.demo.user.service.UserService;

import static rabbit.umc.com.config.apiPayload.BaseResponseStatus.*;
import static rabbit.umc.com.demo.base.Status.*;
import static rabbit.umc.com.demo.converter.MainMissionConverter.*;
import static rabbit.umc.com.demo.converter.MainMissionConverter.toMainMissionViewRes;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MainMissionService {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final MainMissionRepository mainMissionRepository;
    private final MainMissionProofRepository mainMissionProofRepository;
    private final LikeMissionProofRepository likeMissionProofRepository;
    private final CategoryRepository categoryRepository;
    private final MainMissionUsersRepository mainMissionUsersRepository;
    private final UserQueryService userQueryService;
    private final UserService userService;
    private final ReportService reportService;
    private final ImageService imageService;
    private final CategoryService categoryService;

    private List<MainMissionProof> findMainMissionProofByDay(MainMission mainMission, int day, Long mainMissionId){
        LocalDateTime startDate = mainMission.getStartAt().atStartOfDay();
        LocalDateTime targetDate = startDate.plusDays(day - 1);
        LocalDateTime endDate = targetDate.plusDays(1);
        return mainMissionProofRepository.findAllByMainMissionIdAndCreatedAtBetween(mainMissionId, targetDate, endDate);
    }

    private List<RankDto> getRank(Long mainMissionId){
        List<MainMissionUsers> top3 = mainMissionUsersRepository.findTop3OByMainMissionIdOrderByScoreDesc(mainMissionId);
        return top3.stream()
                .map(RankConverter::toRankDto)
                .collect(Collectors.toList());
    }

    private void setLikesForMissionProofImages(List<MissionProofImageDto> missionProofImages, User user) {
        List<LikeMissionProof> likeMissionProofs = likeMissionProofRepository.findLikeMissionProofByUser(user);
        for (MissionProofImageDto imageDto : missionProofImages) {
            boolean isLiked = likeMissionProofs
                    .stream()
                    .anyMatch(likeProof -> likeProof.getMainMissionProof().getId().equals(imageDto.getImageId()));
            if (isLiked) {
                imageDto.setIsLike();
            }
        }
    }

    public GetMainMissionRes getMainMission(Long mainMissionId, int day, Long userId) throws BaseException {
        try {
            MainMission mainMission = mainMissionRepository.getReferenceById(mainMissionId);

            // 해당하는 일차의 인증 사진 가져오기
            List<MainMissionProof> mainMissionProofs = findMainMissionProofByDay(mainMission, day, mainMissionId);
            List<MissionProofImageDto> missionProofImages = toMissionProofImageDto(mainMissionProofs);

            // 좋아요 처리
            User user = userQueryService.getUser(userId);
            setLikesForMissionProofImages(missionProofImages, user);

            //mainMissionId 메인 미션 랭킹 가져오기
            List<RankDto> rankList = getRank(mainMissionId);

            return toGetMainMissionRes(mainMission, missionProofImages, rankList);
        } catch (EntityNotFoundException e) {
            throw new BaseException(DONT_EXIST_MISSION);
        }
    }

    @Transactional
    public void increaseLikeScore(MainMissionProof mainMissionProof){
        MainMissionUsers missionUsers = mainMissionUsersRepository.findMainMissionUsersByUserAndAndMainMission(mainMissionProof.getUser(), mainMissionProof.getMainMission());
        missionUsers.addLikeScore();
        mainMissionUsersRepository.save(missionUsers);
    }

    @Transactional
    public void likeMissionProof(Long userId, Long mainMissionProofId) throws BaseException {
        try {
            MainMissionProof mainMissionProof = mainMissionProofRepository.getReferenceById(mainMissionProofId);
            User user = userQueryService.getUser(userId);

            likeMissionProofRepository.findLikeMissionProofByUserAndMainMissionProofId(user, mainMissionProofId)
                    .ifPresent( m -> {throw new IllegalStateException("이미 좋아요 눌른 사진");});

            // 좋아요 1점 증가
            increaseLikeScore(mainMissionProof);

            //좋아요 여부 저장
            likeMissionProofRepository.save(toLikeMissionProof(user, mainMissionProof));
        } catch (EntityNotFoundException e) {
            throw new BaseException(DONT_EXIST_MISSION_PROOF);
        }catch (IllegalStateException m){
            throw new BaseException(FAILED_TO_LIKE_MISSION);
        }
    }

    @Transactional
    public void decreaseLikeScore(MainMissionProof mainMissionProof){
        MainMissionUsers missionUsers = mainMissionUsersRepository.findMainMissionUsersByUserAndAndMainMission(mainMissionProof.getUser(), mainMissionProof.getMainMission());
        missionUsers.unLikeScore();
        mainMissionUsersRepository.save(missionUsers);
    }

    @Transactional
    public void unLikeMissionProof(Long userId, Long mainMissionProofId) throws BaseException {
        try {
            MainMissionProof mainMissionProof = mainMissionProofRepository.getReferenceById(mainMissionProofId);
            //인증사진 존재 체크
            if (mainMissionProof.getProofImage() == null) {
                throw new EntityNotFoundException("Unable to find proofId with id:" + mainMissionProofId);
            }
            User user = userQueryService.getUser(userId);
            Optional<LikeMissionProof> findLikeMissionProof = likeMissionProofRepository.findLikeMissionProofByUserAndMainMissionProofId(user, mainMissionProofId);

            if (findLikeMissionProof.isEmpty()) throw new BaseException(FAILED_TO_UNLIKE_MISSION);

            // 좋아요 1점 감소 로직
            decreaseLikeScore(mainMissionProof);
            //좋아요 삭제
            likeMissionProofRepository.delete(findLikeMissionProof.get());

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
            User user = userQueryService.getUser(userId);
            if (reportService.isReport(userId, mainMissionProofId)) {
                throw new BaseException(FAILED_TO_REPORT);
            }
            //신고 저장
            reportService.reportMissionProof(ReportConverter.toMissionProofReport(user, mainMissionProof));
            //신고 횟수 15회 이상시 비활성화 처리
            if (reportService.checkInactivation(mainMissionProofId, mainMissionProof)){
                mainMissionProof.inActive();
                mainMissionProofRepository.save(mainMissionProof);
            }

        } catch (EntityNotFoundException e) {
            throw new BaseException(DONT_EXIST_MISSION_PROOF);
        }
    }

    public boolean isCategoryUser(Long userId, Category category){
        return category.getUser().getId() == userId;
    }

    @Transactional
    public void inactivateLastMission(MainMission lastMission){
        if (lastMission != null) {
            lastMission.inActive();
            mainMissionRepository.save(lastMission);
        }
    }
    @Transactional
    public void createMainMission(Long userId, Long categoryId, PostMainMissionReq postMainMissionReq) throws BaseException {
        if (!userQueryService.isHostUser(userId)) {
            throw new BaseException(FORBIDDEN);
        }
        Category category = categoryRepository.getReferenceById(categoryId);
        if (!isCategoryUser(userId, category)) {
            throw new BaseException(FORBIDDEN);
        }

        MainMission lastMission = mainMissionRepository.findMainMissionByCategoryAndStatus(category, ACTIVE);
        // 아전미션이 끝나지 않았다면 새 미션 생성 불가능
        if (lastMission.getEndAt().isAfter(LocalDate.now()) ){
            throw new BaseException(NOT_DONE_MISSION);
        }
        //해당 카테고리 이전 미션 존재 시 이전 미션은 비활성화
        inactivateLastMission(lastMission);
        mainMissionRepository.save(toMainMission(postMainMissionReq, category));
    }

    @Transactional
    public void uploadProofImage(MultipartFile multipartFile, Long categoryId, Long userId) throws BaseException, IOException {


        MainMission mainMission = mainMissionRepository.findMainMissionByCategoryIdAndStatus(categoryId, ACTIVE);
        User user = userQueryService.getUser(userId);

        // 메인 미션 참여 아직 안했으면 참여 시키기
        MainMissionUsers findUser = mainMissionUsersRepository.findMainMissionUsersByUserAndAndMainMission(user, mainMission);
        if (findUser == null) {
            mainMissionUsersRepository.save(toMainMissionUsers(user,  mainMission));
        }
        //만약 당일 이미 사진을 올렸으면 리젝
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDateTime.now().toLocalDate().atTime(LocalTime.MAX);
        List<MainMissionProof> proof = mainMissionProofRepository.findAllByUserAndCreatedAtBetween(user, startOfDay, endOfDay);
        if (!proof.isEmpty()) {
            throw new BaseException(FAILED_TO_UPLOAD);
        }
        String filePath = imageService.createImageUrl(multipartFile, "mission");
        // 10점 점수 획득
        increaseUploadScore(user, mainMission);
        //인증 사진 저장
        mainMissionProofRepository.save(toMainMissionProof(filePath, user, mainMission));
    }

    @Transactional
    public void increaseUploadScore(User user,MainMission mainMission){
        MainMissionUsers missionUsers = mainMissionUsersRepository.findMainMissionUsersByUserAndAndMainMission(user, mainMission);
        missionUsers.addProofScore();
        mainMissionUsersRepository.save(missionUsers);
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
                Long beforeUserId = mainMission.getCategory().getUser().getId();
                User beforeUser =userQueryService.getUser(beforeUserId);
                userService.changePermissionToUser(beforeUser);

                MainMissionUsers topScorer = topScorers.get(0);
                //유저 권한 변경
                User newUser = topScorer.getUser();
                userService.changePermissionToHost(newUser);

                //해당 카테고리 묘집사 변경
                changeCategoryHost(mainMission, newUser);
                mainMission.setLastMission(Boolean.FALSE);
                mainMissionRepository.save(mainMission);
            }
        }
    }

    @Transactional
    public void changeCategoryHost(MainMission mainMission, User newUser){
        Category category = mainMission.getCategory();
        category.changeHostUser(newUser);
        categoryRepository.save(category);
    }

    public List<MainMissionViewRes> getMainMissionView(Long categoryId, Long userId) throws BaseException {
        User user = userQueryService.getUser(userId);
        Category category = categoryService.getCategory(categoryId);
        List<MainMission> mainMissions = category.getMainMissions();

        return mainMissions.stream()
                .map(mainMission -> toMainMissionViewRes(user, mainMission))
                .collect(Collectors.toList());
    }

}



