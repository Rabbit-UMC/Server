package rabbit.umc.com.demo.mission.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rabbit.umc.com.demo.mission.MissionUsers;

import java.util.List;

@Repository
public interface MissionUsersRepository extends JpaRepository<MissionUsers,Long> {

    List<MissionUsers> getMissionUsersByUserId(Long userId);

    MissionUsers getMissionUsersByMissionIdAndUserId(long missionId,long userId);

}
