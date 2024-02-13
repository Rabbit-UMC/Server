package rabbit.umc.com.demo.schedule.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rabbit.umc.com.demo.schedule.domain.Schedule;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
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

//    @Query("select s from Schedule s where month(s.endAt) = :month and s.user.id = :userId order by s.endAt asc")
    @Query("select s from Schedule s where s.user.id= :userId and year(s.endAt) = :year and month(s.endAt) = :month order by  s.endAt asc")
    List<Schedule> findSchedulesByMonthOrderByEndAt(@Param(value = "month") int month, @Param(value = "userId") Long userId, @Param(value = "year") int year);
    @Query("select count(*) from Schedule s where DATE(s.endAt)= :endDate and s.user.id = :userId ")
    Integer countByEndAtAndUserId(@Param("endDate") Date endDate, @Param("userId") Long userId);
    @Modifying
    @Query("delete from Schedule s where s.id in :scheduleIds")
    void deleteByScheduleIds(List<Long> scheduleIds);
}
