package rabbit.umc.com.demo.mainmission.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rabbit.umc.com.config.apiPayload.BaseException;
import rabbit.umc.com.config.apiPayload.BaseResponseStatus;
import rabbit.umc.com.demo.mainmission.domain.MainMission;
import rabbit.umc.com.demo.mainmission.domain.mapping.LikeMissionProof;
import rabbit.umc.com.demo.mainmission.domain.mapping.MainMissionProof;
import rabbit.umc.com.demo.mainmission.repository.LikeMissionProofRepository;
import rabbit.umc.com.demo.user.Domain.User;

@Service
@Transactional
@RequiredArgsConstructor
public class LikeMissionProofService {

    private final LikeMissionProofRepository likeMissionProofRepository;

    public LikeMissionProof getMyLikeMissionProof(User user, MainMissionProof mainMissionProof) throws BaseException {
        return likeMissionProofRepository.findLikeMissionProofByUserAndMainMissionProof(user, mainMissionProof)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.DONT_EXIST_MAIN_MISSION_LIKE));
    }

    public List<LikeMissionProof> getMyLikeMissionProofList(User user, MainMission mainMission) {
        return likeMissionProofRepository.findLikeMissionProofByUserAndMainMission(user, mainMission);
    }

    public Boolean isLikeMissionProof(User user, MainMissionProof mainMissionProof) {
        return likeMissionProofRepository.findLikeMissionProofByUserAndMainMissionProof(user, mainMissionProof)
                .isPresent();
    }

    public void save(LikeMissionProof likeMissionProof) {
        likeMissionProofRepository.save(likeMissionProof);
    }

    public void delete(LikeMissionProof likeMissionProof) {
        likeMissionProofRepository.delete(likeMissionProof);
    }

}
