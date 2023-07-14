package rabbit.umc.com.demo.schedule.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import rabbit.umc.com.demo.Status;
import rabbit.umc.com.demo.schedule.domain.MissionSchedule;
import rabbit.umc.com.demo.schedule.dto.ScheduleDetailRes;

import java.util.List;

public interface MissionScheduleRepository extends JpaRepository<MissionSchedule,Long> {
//    @Query("select ms.mission.id from MissionSchedule ms join Schedule s on s.id = ms.schedule.id")
//    MissionSchedule findMissionIdByScheduleId(Long scheduleId);

    MissionSchedule getMissionScheduleByScheduleId(long scheduleId);

    void deleteByScheduleId(Long id);

    MissionSchedule findMissionScheduleByScheduleId(Long scheduleId);
    List<MissionSchedule> findMissionSchedulesByMissionId(Long missionId);
//    List<MissionSchedule> getMissionScheduleByMissionIdAndStatusIs(Long id, Status status);

    List<MissionSchedule> getMissionScheduleByMissionId(Long id);
}
