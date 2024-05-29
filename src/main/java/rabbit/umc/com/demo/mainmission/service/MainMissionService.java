package rabbit.umc.com.demo.mainmission.service;

import java.time.LocalDate;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rabbit.umc.com.config.apiPayload.BaseException;
import rabbit.umc.com.demo.community.domain.Category;
import rabbit.umc.com.demo.mainmission.domain.MainMission;
import rabbit.umc.com.demo.mainmission.repository.MainMissionRepository;
import java.util.List;

import static rabbit.umc.com.config.apiPayload.BaseResponseStatus.*;
import static rabbit.umc.com.demo.base.Status.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MainMissionService {

    private final MainMissionRepository mainMissionRepository;

    public List<MainMission> getActiveMainMissionList(){
        return mainMissionRepository.findProgressMissionByStatus(ACTIVE);
    }

    public MainMission getActiveMainMissionByCategory(Category category) throws BaseException {
        return mainMissionRepository.findMainMissionsByCategoryAndStatus(category, ACTIVE)
                .orElseThrow(() -> new BaseException(DONT_EXIST_MISSION));
    }

    public MainMission getMainMission(Long mainMissionId) throws BaseException {
        return mainMissionRepository.findById(mainMissionId)
                .orElseThrow(() -> new BaseException(DONT_EXIST_MISSION));
    }

    public MainMission getMainMission(Category category) throws BaseException {
        return mainMissionRepository.findMainMissionByCategoryAndStatus(category, ACTIVE)
                .orElseThrow(() -> new BaseException(DONT_EXIST_MISSION));
    }

    public Optional<MainMission> getOptionalMainMission(Category category){
        return mainMissionRepository.findMainMissionByCategoryAndStatus(category, ACTIVE);
    }

    public MainMission save(MainMission mainMission){
        return mainMissionRepository.save(mainMission);
    }

    public List<MainMission> getCompleteMissions(){
        return mainMissionRepository.findMainMissionsByEndAtBeforeAndLastMissionTrue(LocalDate.now());
    }

    public void closeMainMission(MainMission mainMission){
        mainMission.changeLastMission(Boolean.FALSE);
        mainMissionRepository.save(mainMission);
    }

}



