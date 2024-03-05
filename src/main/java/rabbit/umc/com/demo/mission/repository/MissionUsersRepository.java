package rabbit.umc.com.demo.mission.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rabbit.umc.com.demo.base.Status;
import rabbit.umc.com.demo.mission.Mission;
import rabbit.umc.com.demo.mission.MissionUsers;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Repository
public interface MissionUsersRepository extends JpaRepository<MissionUsers,Long> {

    List<MissionUsers> getMissionUsersByUserId(Long userId);
    List<MissionUsers> getMissionUsersByUserIdAndMissionEndAtIsBefore(Long userId, LocalDateTime now);

    MissionUsers getMissionUsersByMissionIdAndUserId(long missionId,long userId);

    @Query("select mu from MissionUsers mu where mu.mission.id in :missionIds and mu.user.id = :userId")
    List<MissionUsers> getMissionUsersByMissionIdAndUserId(@Param("missionIds") List<Long> missionIds,@Param("userId") long userId);


    void deleteByMissionIdAndUserId(Long missionId, long userId);

    List<MissionUsers> getMissionUsersByUserIdAndMissionEndAtIsAfterAndMissionStatusAndMissionIsOpen(long userId, LocalDateTime currentDateTime, Status active, int isOpen);
}
