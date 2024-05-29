package rabbit.umc.com.demo.mainmission.facade;

import static rabbit.umc.com.config.apiPayload.BaseResponseStatus.FAILED_TO_LIKE_MISSION;
import static rabbit.umc.com.config.apiPayload.BaseResponseStatus.FAILED_TO_REPORT;
import static rabbit.umc.com.config.apiPayload.BaseResponseStatus.FAILED_TO_UNLIKE_MISSION;
import static rabbit.umc.com.config.apiPayload.BaseResponseStatus.FORBIDDEN;
import static rabbit.umc.com.config.apiPayload.BaseResponseStatus.NOT_DONE_MISSION;
import static rabbit.umc.com.demo.converter.MainMissionConverter.toGetMainMissionRes;
import static rabbit.umc.com.demo.converter.MainMissionConverter.toLikeMissionProof;
import static rabbit.umc.com.demo.converter.MainMissionConverter.toMainMission;
import static rabbit.umc.com.demo.converter.MainMissionConverter.toMainMissionProof;
import static rabbit.umc.com.demo.converter.MainMissionConverter.toMissionProofImageDto;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import rabbit.umc.com.config.apiPayload.BaseException;
import rabbit.umc.com.demo.community.category.CategoryService;
import rabbit.umc.com.demo.community.domain.Category;
import rabbit.umc.com.demo.converter.MainMissionConverter;
import rabbit.umc.com.demo.converter.RankConverter;
import rabbit.umc.com.demo.converter.ReportConverter;
import rabbit.umc.com.demo.image.service.ImageService;
import rabbit.umc.com.demo.mainmission.domain.MainMission;
import rabbit.umc.com.demo.mainmission.domain.mapping.LikeMissionProof;
import rabbit.umc.com.demo.mainmission.domain.mapping.MainMissionProof;
import rabbit.umc.com.demo.mainmission.domain.mapping.MainMissionUsers;
import rabbit.umc.com.demo.mainmission.dto.GetMainMissionRes;
import rabbit.umc.com.demo.mainmission.dto.GetMainMissionRes.MissionProofImageDto;
import rabbit.umc.com.demo.mainmission.dto.GetMainMissionRes.RankDto;
import rabbit.umc.com.demo.mainmission.dto.MainMissionViewRes;
import rabbit.umc.com.demo.mainmission.dto.PostMainMissionReq;
import rabbit.umc.com.demo.mainmission.service.LikeMissionProofService;
import rabbit.umc.com.demo.mainmission.service.MainMissionProofService;
import rabbit.umc.com.demo.mainmission.service.MainMissionService;
import rabbit.umc.com.demo.mainmission.service.MainMissionUserService;
import rabbit.umc.com.demo.report.ReportService;
import rabbit.umc.com.demo.user.Domain.User;
import rabbit.umc.com.demo.user.service.UserQueryService;

@Component
@Transactional
@RequiredArgsConstructor
public class MainMissionFacade {

    private final MainMissionService mainMissionService;
    private final MainMissionUserService mainMissionUserService;
    private final LikeMissionProofService likeMissionProofService;
    private final MainMissionProofService mainMissionProofService;
    private final UserQueryService userQueryService;
    private final CategoryService categoryService;
    private final ReportService reportService;
    private final ImageService imageService;

    public GetMainMissionRes getMainMissionByDay(Long missionId, int day, Long userId) throws BaseException {

        MainMission mainMission =mainMissionService.getMainMission(missionId);
        List<MainMissionProof> mainMissionProofs = mainMissionProofService.getMainMissionProofByDay(mainMission, day);
        List<MissionProofImageDto> missionProofImages = toMissionProofImageDto(mainMissionProofs);

        User user = userQueryService.getUserByUserId(userId);
        setLikesForMissionProofImages(missionProofImages, user, mainMission);
        List<RankDto> rankList = getMainMissionRankList(mainMission);

        return toGetMainMissionRes(mainMission, missionProofImages, rankList);
    }

    private void setLikesForMissionProofImages(List<MissionProofImageDto> missionProofImages, User user, MainMission mainMission) {

        List<LikeMissionProof> myLikeProofs = likeMissionProofService.getMyLikeMissionProofList(user, mainMission);
        Map<Long, MissionProofImageDto> imageMap = missionProofImages.stream()
                .collect(Collectors.toMap(MissionProofImageDto::getImageId, Function.identity()));

        myLikeProofs.forEach(likeProof -> {
            MissionProofImageDto imageDto = imageMap.get(likeProof.getMainMissionProof().getId());
            if (imageDto != null)
                imageDto.setIsLike();
        });
    }

    private List<RankDto> getMainMissionRankList(MainMission mainMission) {

        List<MainMissionUsers> topUser = mainMissionUserService.getTop3UsersByMainMission(mainMission);

        return topUser.stream()
                .map(RankConverter::toRankDto)
                .collect(Collectors.toList());
    }

    public void likeMissionProof(Long userId, Long mainMissionProofId) throws BaseException {

        MainMissionProof mainMissionProof = mainMissionProofService.getMainMissionProofById(mainMissionProofId);
        User user = userQueryService.getUserByUserId(userId);
        if (likeMissionProofService.isLikeMissionProof(user, mainMissionProof))
            throw new BaseException(FAILED_TO_LIKE_MISSION);

        likeMissionProofService.save(toLikeMissionProof(user, mainMissionProof));
        mainMissionUserService.increaseUserLikeScore(mainMissionProof);
    }

    public void unLikeMissionProof(Long userId, Long mainMissionProofId) throws BaseException {

        MainMissionProof mainMissionProof = mainMissionProofService.getMainMissionProofById(mainMissionProofId);
        User user = userQueryService.getUserByUserId(userId);
        if (!likeMissionProofService.isLikeMissionProof(user, mainMissionProof))
            throw new BaseException(FAILED_TO_UNLIKE_MISSION);

        LikeMissionProof likeMissionProof = likeMissionProofService.getMyLikeMissionProof(user, mainMissionProof);
        likeMissionProofService.delete(likeMissionProof);

        mainMissionUserService.decreaseLikeScore(mainMissionProof);
    }

    public void reportMissionProof(Long userId, Long mainMissionProofId) throws BaseException {

        MainMissionProof mainMissionProof = mainMissionProofService.getMainMissionProofById(mainMissionProofId);
        User user = userQueryService.getUserByUserId(userId);
        if (reportService.isReport(userId, mainMissionProofId))
            throw new BaseException(FAILED_TO_REPORT);

        reportService.reportMissionProof(ReportConverter.toMissionProofReport(user, mainMissionProof));
        //신고 횟수 15회 이상시 비활성화 처리
        if (reportService.checkInactivation(mainMissionProof)){
            mainMissionProof.inActive();
            mainMissionProofService.save(mainMissionProof);
        }
    }

    public void createMainMission(Long userId, Long categoryId, PostMainMissionReq postMainMissionReq) throws BaseException {
        User user = userQueryService.getUserByUserId(userId);
        Category category = categoryService.getCategory(categoryId);
        if (!userQueryService.isHostUser(userId) || !category.getUser().getId().equals(userId)) // HOST 권한 확인
            throw new BaseException(FORBIDDEN);

        Optional<MainMission> lastMission = mainMissionService.getOptionalMainMission(category);
        if (lastMission.isPresent()) {
            if (lastMission.get().getEndAt().isAfter(LocalDate.now())) // 아전미션이 끝나지 않았다면 새 미션 생성 불가능
                throw new BaseException(NOT_DONE_MISSION);
            //해당 카테고리에 끝난 이전 미션 존재 시 이전 미션은 비활성화
            lastMission.get().inActive();
            mainMissionService.save(lastMission.get());
        }
        mainMissionService.save(toMainMission(postMainMissionReq, category, user));
    }

    public void uploadProofImage(MultipartFile multipartFile, Long categoryId, Long userId) throws BaseException, IOException {

        Category category = categoryService.getCategory(categoryId);
        MainMission mainMission = mainMissionService.getMainMission(category);
        User user = userQueryService.getUserByUserId(userId);

        mainMissionUserService.participateInMainMissionIfNotAlready(user, mainMission);
        mainMissionProofService.checkIfUserUploadedToday(user, mainMission);

        String filePath = imageService.createImageUrl(multipartFile, "mission");
        mainMissionUserService.increaseUploadScore(user, mainMission);

        mainMissionProofService.save(toMainMissionProof(filePath, user, mainMission));
    }

    public List<MainMissionViewRes> getMainMissionView(Long categoryId, Long userId) throws BaseException {
        Category category = categoryService.getCategory(categoryId);
        if (!userQueryService.isHostUser(userId) || !category.getUser().getId().equals(userId)) // HOST 권한 확인
            throw new BaseException(FORBIDDEN);

        List<MainMission> mainMissions = category.getMainMissions();
        return mainMissions.stream()
                .map(MainMissionConverter::toMainMissionViewRes)
                .collect(Collectors.toList());
    }
}
