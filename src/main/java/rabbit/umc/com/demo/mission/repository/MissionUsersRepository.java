package rabbit.umc.com.demo.mission.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rabbit.umc.com.demo.mission.Mission;
import rabbit.umc.com.demo.mission.MissionUsers;

import java.util.List;

@Repository
public interface MissionUsersRepository extends JpaRepository<MissionUsers,Long> {

    List<MissionUsers> getMissionUsersByUserId(Long userId);

    MissionUsers getMissionUsersByMissionIdAndUserId(long missionId,long userId);

    @Query("select mu from MissionUsers mu where mu.mission.id in :missionIds and mu.user.id = :userId")
    List<MissionUsers> getMissionUsersByMissionIdAndUserId(@Param("missionIds") List<Long> missionIds,@Param("userId") long userId);


    void deleteByMissionIdAndUserId(Long missionId, long userId);
}
