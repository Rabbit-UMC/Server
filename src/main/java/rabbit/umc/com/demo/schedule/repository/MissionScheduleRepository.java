package rabbit.umc.com.demo.schedule.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import rabbit.umc.com.demo.schedule.domain.MissionSchedule;
import rabbit.umc.com.demo.schedule.dto.ScheduleDetailRes;

public interface MissionScheduleRepository extends JpaRepository<MissionSchedule,Long> {
//    @Query("select ms.mission.id from MissionSchedule ms join Schedule s on s.id = ms.schedule.id")
//    MissionSchedule findMissionIdByScheduleId(Long scheduleId);

    @Query(value ="select ms from MissionSchedule ms WHERE ms.schedule.id = :scheduleId")
    MissionSchedule getScheduleDetail(@Param("scheduleId") Long scheduleId);
}
