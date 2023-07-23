package rabbit.umc.com.demo.mainmission;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rabbit.umc.com.config.BaseException;
import rabbit.umc.com.config.BaseResponse;
import rabbit.umc.com.config.BaseResponseStatus;
import rabbit.umc.com.demo.Status;
import rabbit.umc.com.demo.article.CategoryRepository;
import rabbit.umc.com.demo.article.domain.Category;
import rabbit.umc.com.demo.mainmission.domain.LikeMissionProof;
import rabbit.umc.com.demo.mainmission.domain.MainMission;
import rabbit.umc.com.demo.mainmission.domain.MainMissionProof;
import rabbit.umc.com.demo.mainmission.domain.MainMissionUsers;
import rabbit.umc.com.demo.mainmission.dto.GetMainMissionRes;
import rabbit.umc.com.demo.mainmission.dto.MissionProofImageDto;
import rabbit.umc.com.demo.mainmission.dto.PostMainMissionReq;
import rabbit.umc.com.demo.mainmission.dto.RankDto;
import rabbit.umc.com.demo.report.Report;
import rabbit.umc.com.demo.report.ReportRepository;
import rabbit.umc.com.demo.user.Domain.User;
import rabbit.umc.com.demo.user.Domain.UserPermision;
import rabbit.umc.com.demo.user.UserRepository;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static rabbit.umc.com.config.BaseResponseStatus.*;
import static rabbit.umc.com.demo.Status.*;
import static rabbit.umc.com.demo.user.Domain.UserPermision.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MainMissionService {
    private final MainMissionRepository mainMissionRepository;
    private final MainMissionProofRepository mainMissionProofRepository;
    private final UserRepository userRepository;
    private final LikeMissionProofRepository likeMissionProofRepository;
    private final ReportRepository reportRepository;
    private final CategoryRepository categoryRepository;
    private final MainMissionUsersRepository mainMissionUsersRepository;

    public GetMainMissionRes getMainMission(Long mainMissionId, int day) throws BaseException {
        try {
            // 메인 미션 찾기
            MainMission mainMission = mainMissionRepository.getReferenceById(mainMissionId);

            // 해당 일차의 인증 사진 가져오기
            LocalDateTime startDate = mainMission.getStartAt().atStartOfDay();
            LocalDateTime targetDate = startDate.plusDays(day -1);
            LocalDateTime endDate = targetDate.plusDays(1);

            List<MainMissionProof> mainMissionProofs = mainMissionProofRepository.findAllByMainMissionIdAndCreatedAtBetween(mainMissionId, targetDate, endDate);
            List<MissionProofImageDto> missionProofImages = mainMissionProofs.stream()
                    .map(MissionProofImageDto::toMissionProofImageDto)
                    .collect(Collectors.toList());
            GetMainMissionRes getMainMissionRes = new GetMainMissionRes(mainMission);
            getMainMissionRes.setMissionProofImages(missionProofImages);

            //메인 미션 랭킹 가져오기
//            List<MainMissionProof> top3 = mainMissionProofRepository.findTop3ByMainMissionIdOrderByLikeCountDesc(mainMissionId);

            List<MainMissionUsers> top3 = mainMissionUsersRepository.findTop3OByMainMissionIdOrderByScoreDesc(mainMissionId);
            List<RankDto> rankList = new ArrayList<>();
            for (MainMissionUsers user : top3) {
                RankDto rankDto = new RankDto();
                rankDto.setUserId(user.getUser().getId());
                rankDto.setUserName(user.getUser().getUserName());
                rankList.add(rankDto);
            }




            getMainMissionRes.setRank(rankList);
            return getMainMissionRes;
        }catch (EntityNotFoundException e){
            throw new BaseException(DONT_EXIST_MISSION);
        }
    }

    @Transactional
    public void likeMissionProof(Long userId, Long mainMissionProofId)throws BaseException{
        try {
            MainMissionProof mainMissionProof = mainMissionProofRepository.getReferenceById(mainMissionProofId);
            if(mainMissionProof.getProofImage() == null){
                throw new EntityNotFoundException("Unable to find proofId with id:" + mainMissionProofId);
            }
            User user = userRepository.getReferenceById(userId);
            LikeMissionProof findLikeMissionProof = likeMissionProofRepository.findLikeMissionProofByUserAndMainMissionProofId(user,mainMissionProofId);
            if(findLikeMissionProof !=null){
                throw new BaseException(FAILED_TO_LIKE_MISSION);
            }


            // 1점 추가
            MainMissionUsers missionUsers = mainMissionUsersRepository.findMainMissionUsersByUserAndAndMainMission(mainMissionProof.getUser(), mainMissionProof.getMainMission());
            missionUsers.addLikeScore();
            mainMissionUsersRepository.save(missionUsers);

            LikeMissionProof likeMissionProof = new LikeMissionProof();
            likeMissionProof.setUser(user);
            likeMissionProof.setMainMissionProof(mainMissionProof);

            likeMissionProofRepository.save(likeMissionProof);


        }catch (EntityNotFoundException e){
            throw new BaseException(DONT_EXIST_MISSION_PROOF);
        }
    }

    @Transactional
    public void unLikeMissionProof(Long userId, Long mainMissionProofId) throws BaseException{
        try{
            MainMissionProof mainMissionProof = mainMissionProofRepository.getReferenceById(mainMissionProofId);
            if(mainMissionProof.getProofImage() == null){
                throw new EntityNotFoundException("Unable to find proofId with id:" + mainMissionProofId);
            }
            User user = userRepository.getReferenceById(userId);
            LikeMissionProof findLikeMissionProof = likeMissionProofRepository.findLikeMissionProofByUserAndMainMissionProofId(user,mainMissionProofId);
            if(findLikeMissionProof == null){
                throw new BaseException(FAILED_TO_UNLIKE_MISSION);
            }

            //1점 삭제
            MainMissionUsers missionUsers = mainMissionUsersRepository.findMainMissionUsersByUserAndAndMainMission(mainMissionProof.getUser(), mainMissionProof.getMainMission());
            missionUsers.unLikeScore();
            mainMissionUsersRepository.save(missionUsers);

            likeMissionProofRepository.delete(findLikeMissionProof);
        }catch (EntityNotFoundException e){
            throw new BaseException(DONT_EXIST_MISSION_PROOF);
        }
    }


    @Transactional
    public void reportMissionProof(Long userId, Long mainMissionProofId) throws BaseException{
        try {
            MainMissionProof mainMissionProof = mainMissionProofRepository.getReferenceById(mainMissionProofId);
            if(mainMissionProof.getProofImage() ==null){
                throw new EntityNotFoundException("Unable to find proofId with id:" + mainMissionProofId);
            }
            User user = userRepository.getReferenceById(userId);
            Report findReport = reportRepository.findReportByUserIdAndAndMainMissionProofId(userId, mainMissionProofId);
            if (findReport != null){
                throw new BaseException(FAILED_TO_REPORT);
            }
            Report report = new Report();
            report.setUser(user);
            report.setMainMissionProof(mainMissionProof);
            reportRepository.save(report);

            //신고 횟수 15회 이상시 비활성화
            List<Report> countReport = reportRepository.findAllByMainMissionProofId(mainMissionProofId);
            if(countReport.size() > 14){
                mainMissionProof.setStatus(INACTIVE);
            }

        }catch (EntityNotFoundException e){
            throw new BaseException(DONT_EXIST_MISSION_PROOF);
        }

    }

    @Transactional
    public void createMainMission(Long userId, Long categoryId, PostMainMissionReq postMainMissionReq) throws BaseException {

        //유저 자격 확인
        User user = userRepository.getReferenceById(userId);
        if(user.getUserPermission() != HOST){
            throw new  BaseException(INVALID_JWT);
        }

        //해당 카테고리 자격 확인
        Category category = categoryRepository.getReferenceById(categoryId);
        if(category.getUserId() != userId) {
            throw new BaseException(INVALID_JWT);
        }

        //이전 미션 비활성화
        MainMission lastMission = mainMissionRepository.findMainMissionByCategoryAndStatus(category, ACTIVE);
        lastMission.setStatus(INACTIVE);
        mainMissionRepository.save(lastMission);

        //메인 미션 생성
        MainMission mainMission = new MainMission();
        mainMission.setMainMission(postMainMissionReq,category);

        //db저장
        mainMissionRepository.save(mainMission);

    }

    @Transactional
    public void uploadProofImage(Long categoryId, Long userId, String filePath) throws BaseException {
        MainMission mainMission = mainMissionRepository.findMainMissionByCategoryIdAndStatus(categoryId, ACTIVE);
        User user = userRepository.getReferenceById(userId);

        // 메인 미션 참여 아직 안했으면 참여
        MainMissionUsers findUser = mainMissionUsersRepository.findMainMissionUsersByUserAndAndMainMission(user, mainMission);
        if (findUser == null){
            MainMissionUsers mainMissionUsers = new MainMissionUsers();
            mainMissionUsers.setMainMissionUsers(user, mainMission);
            mainMissionUsersRepository.save(mainMissionUsers);
        }

        //만약 당일 이미 사진을 올렸으면 리젝
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDateTime.now().toLocalDate().atTime(LocalTime.MAX);
        List<MainMissionProof> proof = mainMissionProofRepository.findAllByUserAndCreatedAtBetween(user, startOfDay, endOfDay);
        if(!proof.isEmpty()){
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
}

