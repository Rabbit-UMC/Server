package rabbit.umc.com.demo.mainmission.service;

import static rabbit.umc.com.config.apiPayload.BaseResponseStatus.FAILED_TO_UPLOAD;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rabbit.umc.com.config.apiPayload.BaseException;
import rabbit.umc.com.config.apiPayload.BaseResponseStatus;
import rabbit.umc.com.demo.mainmission.domain.MainMission;
import rabbit.umc.com.demo.mainmission.domain.mapping.MainMissionProof;
import rabbit.umc.com.demo.mainmission.repository.MainMissionProofRepository;
import rabbit.umc.com.demo.user.Domain.User;

@Service
@Transactional
@RequiredArgsConstructor
public class MainMissionProofService {

    private final MainMissionProofRepository mainMissionProofRepository;

    public MainMissionProof getMainMissionProofById(Long mainMissionProofId) throws BaseException {
        return mainMissionProofRepository.findById(mainMissionProofId)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.DONT_EXIST_MISSION_PROOF));
    }

    public List<MainMissionProof> getMainMissionProofByDay(MainMission mainMission, int day){

        LocalDateTime missionStartDay = mainMission.getStartAt().atStartOfDay();
        LocalDateTime targetDate = missionStartDay.plusDays(day - 1);
        LocalDateTime endDate = targetDate.plusDays(1);

        return mainMissionProofRepository.findAllByMainMissionIdAndCreatedAtBetween(mainMission.getId(), targetDate, endDate);
    }

    public void save(MainMissionProof mainMissionProof){
        mainMissionProofRepository.save(mainMissionProof);
    }

    //만약 당일 이미 해당 메인 미션에 사진을 올렸으면 리젝
    public void checkIfUserUploadedToday(User user, MainMission mainMission) throws BaseException {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDateTime.now().toLocalDate().atTime(LocalTime.MAX);
        List<MainMissionProof> proof = mainMissionProofRepository.findAllByUserAndMainMissionAndCreatedAtBetween(user, mainMission, startOfDay, endOfDay);
        if (!proof.isEmpty()) {
            throw new BaseException(FAILED_TO_UPLOAD);
        }
    }

}
