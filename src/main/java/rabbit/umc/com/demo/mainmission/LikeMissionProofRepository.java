package rabbit.umc.com.demo.mainmission;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rabbit.umc.com.demo.mainmission.domain.LikeMissionProof;
import rabbit.umc.com.demo.user.Domain.User;

import java.util.List;

@Repository
public interface LikeMissionProofRepository extends JpaRepository<LikeMissionProof , Long> {
    LikeMissionProof findLikeMissionProofByUserAndMainMissionProofId(User user, Long mainMissionProofId);

    List<LikeMissionProof> findLikeMissionProofByUser(User user);

}
