package rabbit.umc.com.demo.schedule.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;
import rabbit.umc.com.demo.schedule.domain.Schedule;

import java.sql.Timestamp;
import java.util.List;

@Repository
@EnableJpaRepositories
public interface ScheduleRepository extends JpaRepository<Schedule,Long> {
    @Query(value = "select s from Schedule s join MissionSchedule ms on ms.schedule.id = s.id where s.status = 'ACTIVE' and s.user.id = :userId order by s.endAt asc")
    List<Schedule> getHome(@Param(value = "userId") long userId);
    List<Schedule> getSchedulesByUserIdOrderByEndAt(long userId);

    @Query(value = "SELECT s FROM Schedule s JOIN MissionSchedule ms ON ms.schedule.id = s.id WHERE DATE(s.startAt) = DATE(:when) and s.user.id = :userId order by s.endAt asc")
    List<Schedule> getScheduleByWhenAndUserId(@Param(value = "when") Timestamp when, @Param(value = "userId") long userId);

    Schedule findScheduleById(Long id);

//    Schedule findScheduleByIdAndUserId(Long id,Long userId);
    @Query("select s from Schedule s where s.id in :scheduleIds and s.user.id = :userId")
    List<Schedule> findSchedulesByIdsAndUserId(@Param(value = "scheduleIds") List<Long> scheduleIds, @Param(value = "userId") long userId);

    Schedule getScheduleByIdAndUserId(Long id, Long userId);

    Schedule findScheduleByIdAndUserId(Long scheduleId, Long userId);

    @Query("select s from Schedule s where month(s.endAt) = :month and s.user.id = :userId order by s.endAt asc")
    List<Schedule> findSchedulesByMonth(@Param(value = "month") Integer month, @Param(value = "userId") Long userId);
}
