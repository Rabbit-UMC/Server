package rabbit.umc.com.demo.mainmission.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rabbit.umc.com.demo.mainmission.domain.MainMission;
import rabbit.umc.com.demo.mainmission.domain.mapping.LikeMissionProof;
import rabbit.umc.com.demo.mainmission.domain.mapping.MainMissionProof;
import rabbit.umc.com.demo.user.Domain.User;

import java.util.List;

@Repository
public interface LikeMissionProofRepository extends JpaRepository<LikeMissionProof , Long> {

    Optional<LikeMissionProof> findLikeMissionProofByUserAndMainMissionProof(User user, MainMissionProof mainMissionProof);

    @Query("SELECT lmp FROM LikeMissionProof lmp WHERE lmp.user = :user AND lmp.mainMissionProof.mainMission = :mainMission")
    List<LikeMissionProof> findLikeMissionProofByUserAndMainMission(@Param("user") User user, @Param("mainMission") MainMission mainMission);

}
