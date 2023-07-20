package rabbit.umc.com.demo.mainmission;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rabbit.umc.com.demo.mainmission.domain.MainMission;
import rabbit.umc.com.demo.mainmission.domain.MainMissionProof;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MainMissionProofRepository extends JpaRepository<MainMissionProof, Long> {
    List<MainMissionProof> findAllByMainMissionIdAndCreatedAtBetween(Long mainMissionId, LocalDateTime startTime, LocalDateTime endTime);


    @Query("SELECT DISTINCT m FROM MainMissionProof m " +
            "JOIN FETCH m.likeMissionProofs l " +
            "WHERE m.mainMission.id = :mainMissionId " +
            "ORDER BY SIZE(m.likeMissionProofs) DESC")
    List<MainMissionProof> findTop3ByMainMissionIdOrderByLikeCountDesc(@Param("mainMissionId") Long mainMissionId);

}
