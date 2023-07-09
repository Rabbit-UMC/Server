package rabbit.umc.com.demo.schedule.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rabbit.umc.com.demo.schedule.domain.Schedule;
import rabbit.umc.com.demo.schedule.dto.PatchScheduleReq;
import rabbit.umc.com.demo.schedule.dto.ScheduleDetailRes;
import rabbit.umc.com.demo.schedule.dto.ScheduleListDto;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Repository
@EnableJpaRepositories
public interface ScheduleRepository extends JpaRepository<Schedule,Long> {
    @Query(value = "select s from Schedule s join MissionSchedule ms on ms.schedule.id = s.id")
    List<Schedule> getHome();

    @Query(value = "SELECT s FROM Schedule s JOIN MissionSchedule ms ON ms.schedule.id = s.id WHERE DATE(s.startAt) = DATE(:when)")
    List<Schedule> getScheduleByWhen(@Param(value = "when") Timestamp when);
}