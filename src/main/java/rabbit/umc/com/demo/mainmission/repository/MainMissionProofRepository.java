package rabbit.umc.com.demo.mainmission.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rabbit.umc.com.demo.mainmission.domain.MainMission;
import rabbit.umc.com.demo.mainmission.domain.mapping.MainMissionProof;
import rabbit.umc.com.demo.user.Domain.User;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MainMissionProofRepository extends JpaRepository<MainMissionProof, Long> {

    List<MainMissionProof> findAllByMainMissionIdAndCreatedAtBetween(Long mainMissionId, LocalDateTime startTime, LocalDateTime endTime);

    List<MainMissionProof> findAllByUserAndMainMissionAndCreatedAtBetween(User user, MainMission mainMission, LocalDateTime startOfDay, LocalDateTime endOfDay);


}
