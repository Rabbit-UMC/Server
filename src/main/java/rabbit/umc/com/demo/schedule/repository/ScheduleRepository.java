package rabbit.umc.com.demo.schedule.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;
import rabbit.umc.com.demo.schedule.Schedule;
import rabbit.umc.com.demo.schedule.dto.ScheduleHomeRes;
import rabbit.umc.com.demo.schedule.dto.ScheduleListDto;

import java.util.List;

@Repository
@EnableJpaRepositories
public interface ScheduleRepository extends JpaRepository<Schedule,Integer> {
    @Query(value = "select s from Schedule s join MissionSchedule ms on ms.schedule.id = s.id")
    List<Schedule> getHome();

}
