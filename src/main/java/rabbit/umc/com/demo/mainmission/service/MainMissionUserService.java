package rabbit.umc.com.demo.mainmission.service;

import static rabbit.umc.com.demo.converter.MainMissionConverter.toMainMissionUsers;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rabbit.umc.com.config.apiPayload.BaseException;
import rabbit.umc.com.config.apiPayload.BaseResponseStatus;
import rabbit.umc.com.demo.mainmission.domain.MainMission;
import rabbit.umc.com.demo.mainmission.domain.mapping.MainMissionProof;
import rabbit.umc.com.demo.mainmission.domain.mapping.MainMissionUsers;
import rabbit.umc.com.demo.mainmission.repository.MainMissionUsersRepository;
import rabbit.umc.com.demo.user.Domain.User;

@Service
@Transactional
@RequiredArgsConstructor
public class MainMissionUserService {

    public final MainMissionUsersRepository mainMissionUsersRepository;


    public List<MainMissionUsers> getTop3UsersByMainMission(MainMission mainMission){
        return mainMissionUsersRepository.findTop3OByMainMissionOrderByScoreDesc(mainMission);
    }

    public MainMissionUsers getMainMissionUsers(User user, MainMission mainMission) throws BaseException {
        return mainMissionUsersRepository.findMainMissionUsersByUserAndAndMainMission(user, mainMission)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.DONT_EXIST_MISSION_USERS));
    }

    public void increaseUserLikeScore(MainMissionProof mainMissionProof) throws BaseException {
        MainMissionUsers missionUsers = getMainMissionUsers(mainMissionProof.getUser(), mainMissionProof.getMainMission());
        missionUsers.addLikeScore();

        mainMissionUsersRepository.save(missionUsers);
    }

    public void decreaseLikeScore(MainMissionProof mainMissionProof) throws BaseException {
        MainMissionUsers missionUsers = getMainMissionUsers(mainMissionProof.getUser(), mainMissionProof.getMainMission());
        missionUsers.unLikeScore();

        mainMissionUsersRepository.save(missionUsers);
    }

    public void increaseUploadScore(User user, MainMission mainMission) throws BaseException {
        MainMissionUsers mainMissionUsers = getMainMissionUsers(user, mainMission);
        mainMissionUsers.addProofScore();
        mainMissionUsersRepository.save(mainMissionUsers);

    }

    // 메인 미션 참여 아직 안했으면 참여 시키기
    public void participateInMainMissionIfNotAlready(User user, MainMission mainMission) {
        Optional<MainMissionUsers> findUser = mainMissionUsersRepository.findMainMissionUsersByUserAndAndMainMission(user, mainMission);
        if (findUser.isEmpty()) {
            mainMissionUsersRepository.save(toMainMissionUsers(user, mainMission));
        }
    }


}
