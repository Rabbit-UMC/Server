package rabbit.umc.com.demo.schedule.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import rabbit.umc.com.demo.schedule.domain.MissionSchedule;

import java.util.List;

public interface MissionScheduleRepository extends JpaRepository<MissionSchedule,Long> {
//    @Query("select ms.mission.id from MissionSchedule ms join Schedule s on s.id = ms.schedule.id")
//    MissionSchedule findMissionIdByScheduleId(Long scheduleId);

    MissionSchedule getMissionScheduleByScheduleId(long scheduleId);

    void deleteByScheduleId(Long id);

    MissionSchedule findMissionScheduleByScheduleId(Long scheduleId);
    List<MissionSchedule> findMissionSchedulesByMissionId(Long missionId);
//    List<MissionSchedule> getMissionScheduleByMissionIdAndStatusIs(Long id, Status status);

    @Query("select ms from MissionSchedule ms where ms.mission.id in :missionIds")
    List<MissionSchedule> findMissionSchedulesByMissionIds(@Param("missionIds") List<Long> missionIds);


    List<MissionSchedule> getMissionScheduleByMissionId(Long id);
    @Modifying
    @Query("delete from MissionSchedule ms where ms.mission.id = :missionId and ms.schedule.id in :scheduleIds")
    void deleteByMissionIdAndScheduleIds(Long missionId, List<Long> scheduleIds);
    @Modifying
    @Query("delete from MissionSchedule ms where ms.schedule.id in :scheduleIds")
    void deleteByScheduleIds(List<Long> scheduleIds);
}
