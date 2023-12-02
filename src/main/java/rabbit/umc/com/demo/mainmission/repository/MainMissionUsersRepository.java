package rabbit.umc.com.demo.mainmission.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rabbit.umc.com.demo.mainmission.domain.MainMission;
import rabbit.umc.com.demo.mainmission.domain.mapping.MainMissionUsers;
import rabbit.umc.com.demo.user.Domain.User;

import java.util.List;

@Repository
public interface MainMissionUsersRepository extends JpaRepository<MainMissionUsers, Long> {
    MainMissionUsers findMainMissionUsersByUserAndAndMainMission(User user, MainMission mainMission);

    List<MainMissionUsers> findTop3OByMainMissionIdOrderByScoreDesc(Long mainMissionId);

    List<MainMissionUsers> findTopScorersByMainMissionOrderByScoreDesc(MainMission mainMission, PageRequest of);

}
