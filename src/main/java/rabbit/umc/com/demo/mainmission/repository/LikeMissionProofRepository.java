package rabbit.umc.com.demo.mainmission.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rabbit.umc.com.demo.mainmission.domain.mapping.LikeMissionProof;
import rabbit.umc.com.demo.user.Domain.User;

import java.util.List;

@Repository
public interface LikeMissionProofRepository extends JpaRepository<LikeMissionProof , Long> {
    Optional<LikeMissionProof> findLikeMissionProofByUserAndMainMissionProofId(User user, Long mainMissionProofId);

    List<LikeMissionProof> findLikeMissionProofByUser(User user);

}
