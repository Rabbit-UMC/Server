package rabbit.umc.com.demo.mainmission;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rabbit.umc.com.demo.mainmission.domain.MainMission;
import rabbit.umc.com.demo.mainmission.domain.MainMissionUsers;
import rabbit.umc.com.demo.user.Domain.User;

import java.util.List;

@Repository
public interface MainMissionUsersRepository extends JpaRepository<MainMissionUsers, Long> {
    MainMissionUsers findMainMissionUsersByUserAndAndMainMission(User user, MainMission mainMission);

    List<MainMissionUsers> findTop3OByMainMissionIdOrderByScoreDesc(Long mainMissionId);

}
